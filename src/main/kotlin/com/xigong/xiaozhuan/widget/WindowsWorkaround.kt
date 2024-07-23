package com.xigong.xiaozhuan.widget

import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Structure
import com.sun.jna.platform.win32.BaseTSD.*
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
private interface User32Ex : User32 {
    fun SetWindowLong(hWnd: HWND, nIndex: Int, wndProc: WindowProc): LONG_PTR
    fun SetWindowLongPtr(hWnd: HWND, nIndex: Int, wndProc: WindowProc): LONG_PTR
    fun CallWindowProc(proc: LONG_PTR, hWnd: HWND, uMsg: Int, uParam: WPARAM, lParam: LPARAM): LRESULT
}

// See https://stackoverflow.com/q/62240901
@Structure.FieldOrder(
    "leftBorderWidth",
    "rightBorderWidth",
    "topBorderHeight",
    "bottomBorderHeight"
)
data class WindowMargins(
    @JvmField var leftBorderWidth: Int,
    @JvmField var rightBorderWidth: Int,
    @JvmField var topBorderHeight: Int,
    @JvmField var bottomBorderHeight: Int
) : Structure(), Structure.ByReference

internal class CustomWindowProcedure(private val windowHandle: HWND) : WindowProc {
    // See https://learn.microsoft.com/en-us/windows/win32/winmsg/about-messages-and-message-queues#system-defined-messages
    private val WM_NCCALCSIZE = 0x0083
    private val WM_NCHITTEST = 0x0084
    private val GWLP_WNDPROC = -4
    private val margins = WindowMargins(
        leftBorderWidth = 0,
        topBorderHeight = 0,
        rightBorderWidth = -1,
        bottomBorderHeight = -1
    )
    private val USER32EX =
        runCatching { Native.load("user32", User32Ex::class.java, W32APIOptions.DEFAULT_OPTIONS) }
            .onFailure { println("Could not load user32 library") }
            .getOrNull()

    // The default window procedure to call its methods when the default method behaviour is desired/sufficient
    private var defaultWndProc = if (is64Bit()) {
        USER32EX?.SetWindowLongPtr(windowHandle, GWLP_WNDPROC, this) ?: LONG_PTR(-1)
    } else {
        USER32EX?.SetWindowLong(windowHandle, GWLP_WNDPROC, this) ?: LONG_PTR(-1)
    }

    init {
        enableResizability()
        enableBorderAndShadow()
        // enableTransparency(alpha = 255.toByte())
    }

    override fun callback(hWnd: HWND, uMsg: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
        return when (uMsg) {
            // Returns 0 to make the window not draw the non-client area (title bar and border)
            // thus effectively making all the window our client area
            WM_NCCALCSIZE -> {
                LRESULT(0)
            }
            // The CallWindowProc function is used to pass messages that
            // are not handled by our custom callback function to the default windows procedure
            WM_NCHITTEST -> {
                USER32EX?.CallWindowProc(defaultWndProc, hWnd, uMsg, wParam, lParam) ?: LRESULT(0)
            }

            WM_DESTROY -> {
                USER32EX?.CallWindowProc(defaultWndProc, hWnd, uMsg, wParam, lParam) ?: LRESULT(0)
            }

            else -> {
                USER32EX?.CallWindowProc(defaultWndProc, hWnd, uMsg, wParam, lParam) ?: LRESULT(0)
            }
        }
    }

    /**
     * For this to take effect, also set `resizable` argument of Compose Window to `true`.
     */
    private fun enableResizability() {
        val style = USER32EX?.GetWindowLong(windowHandle, GWL_STYLE) ?: return
        val newStyle = style or WS_CAPTION
        USER32EX.SetWindowLong(windowHandle, GWL_STYLE, newStyle)
    }

    /**
     * To disable window border and shadow, pass (0, 0, 0, 0) as window margins
     * (or, simply, don't call this function).
     */
    private fun enableBorderAndShadow() {
        val dwmApi = "dwmapi"
            .runCatching(NativeLibrary::getInstance)
            .onFailure { println("Could not load dwmapi library") }
            .getOrNull()
        dwmApi
            ?.runCatching { getFunction("DwmExtendFrameIntoClientArea") }
            ?.onFailure { println("Could not enable window native decorations (border/shadow/rounded corners)") }
            ?.getOrNull()
            ?.invoke(arrayOf(windowHandle, margins))
    }

    /**
     * See https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setlayeredwindowattributes
     * @param alpha 0 for transparent, 255 for opaque
     */
    private fun enableTransparency(alpha: Byte) {
        val defaultStyle = User32.INSTANCE.GetWindowLong(windowHandle, GWL_EXSTYLE)
        val newStyle = defaultStyle or User32.WS_EX_LAYERED
        USER32EX?.SetWindowLong(windowHandle, User32.GWL_EXSTYLE, newStyle)
        USER32EX?.SetLayeredWindowAttributes(windowHandle, 0, alpha, LWA_ALPHA)
    }

    private fun is64Bit(): Boolean {
        val bitMode = System.getProperty("com.ibm.vm.bitmode")
        val model = System.getProperty("sun.arch.data.model", bitMode)
        return model == "64"
    }
}