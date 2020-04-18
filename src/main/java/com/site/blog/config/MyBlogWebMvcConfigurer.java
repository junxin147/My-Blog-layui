package com.site.blog.config;

import com.site.blog.constants.UploadConstants;
import com.site.blog.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class MyBlogWebMvcConfigurer implements WebMvcConfigurer {
    @Value("${uploadFile.resourceHandler}")
    private String resourceHandler;//请求 url 中的资源映射，不推荐写死在代码中，最好提供可配置，如 /uploadFiles/**

    @Value("${uploadFile.location}")
    private String location;//上传文件保存的本地目录，使用@Value获取全局配置文件中配置的属性值，如 E:/wmx/uploadFiles/
    @Value("${uploadFile.linuxlocation}")
    private String linuxlocation;//上传文件保存的linux目录，使用@Value获取全局配置文件中配置的属性值，如 /root/


    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //就是说 url 中出现 resourceHandler 匹配时，则映射到 location 中去,location 相当于虚拟路径
        //映射本地文件时，开头必须是 file:/// 开头，表示协议
        String staruri=location;
        File locationfold=new File(location);
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + location);

        if (!locationfold.exists() && !locationfold.isDirectory()) {
            //判断环境是windows还是linux，确认实际路径
            locationfold=new File(linuxlocation);
            staruri=linuxlocation;
            registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + linuxlocation);
        }



    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/v1/login")
                .excludePathPatterns("/admin/v1/reload")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**")
                .excludePathPatterns("/X-admin/**");
    }
//
//    /**
//     * @Description: 重写addResourceHandlers映射文件路径
//     * @Param: [registry]
//     * @return: void
//     * @date: 2019/8/7 9:06
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/authorImg/**").addResourceLocations("file:" + UploadConstants.UPLOAD_AUTHOR_IMG);
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + UploadConstants.FILE_UPLOAD_DIC);
//    }
}
