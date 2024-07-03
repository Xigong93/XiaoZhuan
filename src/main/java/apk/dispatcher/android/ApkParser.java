package apk.dispatcher.android;

import android.content.res.AXMLResource;
import apk.dispatcher.util.ApkInfo;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Apk解析器
 */
public class ApkParser {

    public static ApkInfo parse(File apkFile) throws Exception {
        // Axml 转 xml
        String xml = getManifestXml(apkFile);
        // xml 转 json
        JSONObject manifest = XML.toJSONObject(xml).getJSONObject("manifest");
        String applicationId = manifest.getString("package");
        long versionCode = 0;
        String versionName = null;
        for (String rawKey : manifest.keySet()) {
            String[] pair = rawKey.trim().split(":");
            switch (pair[pair.length - 1]) {
                case "versionCode" -> {
                    versionCode = manifest.getLong(rawKey);
                }
                case "versionName" -> {
                    versionName = manifest.get(rawKey).toString();// 不一定是String，比如会把1.0,解析成数字
                }
            }
        }
        Objects.requireNonNull(versionName, "解析Apk失败," + apkFile);
        Objects.requireNonNull(applicationId, "解析Apk失败," + apkFile);
        if (versionCode == 0) throw new RuntimeException("解析Apk失败," + apkFile);
        return new ApkInfo(apkFile.getAbsolutePath(), applicationId, versionCode, versionName);
    }

    private static String getManifestXml(File apkFile) {
        try (ZipFile z = new ZipFile(apkFile);
             InputStream is = z.getInputStream(z.getEntry("AndroidManifest.xml"))
        ) {

            AXMLResource axmlResource = new AXMLResource(is);
            String xml = axmlResource.toXML();
            Objects.requireNonNull(xml);
            return xml;
        } catch (IOException e) {
            throw new RuntimeException("解析版本号失败,"+apkFile, e);
        }
    }

}
