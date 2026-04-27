package org.example;

import org.example.common.properties.AliOssProperties;
import org.example.common.utils.AliOssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 阿里云OSS文件上传测试类
 */
@SpringBootTest
public class AliOssUploadTest {

    @Resource
    private AliOssUtil aliOssUtil;

    @Resource
    private AliOssProperties aliOssProperties;

    private byte[] testImageBytes;
    private String testFileName;

    @BeforeEach
    public void setUp() throws IOException {
        // 准备测试数据：创建一个简单的测试文件内容
        testFileName = "test_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        // 模拟一个小的图片文件字节数组（这里用简单的字节数组代替真实图片）
        testImageBytes = "This is a test image content".getBytes();
    }

    /**
     * 测试文件上传功能 - 正常情况
     */
    @Test
    public void testUpload_Success() {
        // 生成唯一的对象名称
        String objectName = generateObjectName(testFileName);
        
        // 执行上传
        String url = aliOssUtil.upload(testImageBytes, objectName);
        
        // 验证返回的URL不为空且格式正确
        assertNotNull(url, "上传返回的URL不应为空");
        assertTrue(url.startsWith("https://"), "URL应以https://开头");
        assertTrue(url.contains(aliOssProperties.getBucketName()), "URL应包含bucket名称");
        assertTrue(url.contains(objectName), "URL应包含对象名称");
        
        System.out.println("文件上传成功，访问URL: " + url);
    }

    /**
     * 测试文件上传功能 - 不同文件类型
     */
    @Test
    public void testUpload_DifferentFileTypes() {
        // 测试文本文件
        String txtFileName = "test_" + System.currentTimeMillis() + ".txt";
        byte[] txtBytes = "测试文本内容".getBytes();
        String txtUrl = aliOssUtil.upload(txtBytes, generateObjectName(txtFileName));
        assertNotNull(txtUrl, "文本文件上传应成功");
        System.out.println("文本文件上传成功: " + txtUrl);

        // 测试PDF文件（模拟）
        String pdfFileName = "test_" + System.currentTimeMillis() + ".pdf";
        byte[] pdfBytes = "%PDF-1.4 test content".getBytes();
        String pdfUrl = aliOssUtil.upload(pdfBytes, generateObjectName(pdfFileName));
        assertNotNull(pdfUrl, "PDF文件上传应成功");
        System.out.println("PDF文件上传成功: " + pdfUrl);
    }

    /**
     * 测试文件上传功能 - 大文件
     */
    @Test
    public void testUpload_LargeFile() {
        // 创建一个较大的字节数组（1MB）
        byte[] largeFileBytes = new byte[1024 * 1024];
        for (int i = 0; i < largeFileBytes.length; i++) {
            largeFileBytes[i] = (byte) (i % 256);
        }
        
        String largeFileName = "large_test_" + System.currentTimeMillis() + ".bin";
        String url = aliOssUtil.upload(largeFileBytes, generateObjectName(largeFileName));
        
        assertNotNull(url, "大文件上传应成功");
        System.out.println("大文件上传成功: " + url);
    }

    /**
     * 测试文件上传功能 - 特殊字符文件名
     */
    @Test
    public void testUpload_SpecialCharacterFileName() {
        String specialFileName = "测试文件_2024_测试.jpg";
        String objectName = generateObjectName(specialFileName);
        
        String url = aliOssUtil.upload(testImageBytes, objectName);
        
        assertNotNull(url, "特殊字符文件名上传应成功");
        System.out.println("特殊字符文件名上传成功: " + url);
    }

    /**
     * 测试从本地文件上传
     */
    @Test
    public void testUpload_FromLocalFile() throws IOException {
        // 创建一个临时测试文件
        File tempFile = File.createTempFile("test_upload_", ".jpg");
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] fileBytes = new byte[(int) tempFile.length()];
            fis.read(fileBytes);
            
            String objectName = generateObjectName(tempFile.getName());
            String url = aliOssUtil.upload(fileBytes, objectName);
            
            assertNotNull(url, "本地文件上传应成功");
            System.out.println("本地文件上传成功: " + url);
        } finally {
            // 清理临时文件
            tempFile.delete();
        }
    }

    /**
     * 测试并发上传
     */
    @Test
    public void testUpload_Concurrent() throws InterruptedException {
        final int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        final String[] results = new String[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                String fileName = "concurrent_test_" + index + "_" + System.currentTimeMillis() + ".jpg";
                byte[] bytes = ("并发测试内容-" + index).getBytes();
                results[index] = aliOssUtil.upload(bytes, generateObjectName(fileName));
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证所有上传都成功
        for (String result : results) {
            assertNotNull(result, "并发上传应全部成功");
            System.out.println("并发上传成功: " + result);
        }
    }

    /**
     * 测试配置参数是否正确加载
     */
    @Test
    public void testConfiguration_Loaded() {
        assertNotNull(aliOssProperties.getEndpoint(), "endpoint应已配置");
        assertNotNull(aliOssProperties.getAccessKeyId(), "accessKeyId应已配置");
        assertNotNull(aliOssProperties.getAccessKeySecret(), "accessKeySecret应已配置");
        assertNotNull(aliOssProperties.getBucketName(), "bucketName应已配置");
        
        System.out.println("OSS配置信息:");
        System.out.println("Endpoint: " + aliOssProperties.getEndpoint());
        System.out.println("BucketName: " + aliOssProperties.getBucketName());
    }

    /**
     * 生成对象名称（带时间戳和UUID，避免重名）
     * 
     * @param originalFileName 原始文件名
     * @return 生成的对象名称
     */
    private String generateObjectName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        // 提取文件扩展名
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        return String.format("uploads/%s_%s%s", timestamp, uuid, extension);
    }
}
