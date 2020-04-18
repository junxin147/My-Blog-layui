package com.site.blog.controller.admin;

import com.site.blog.constants.HttpStatusConstants;
import com.site.blog.constants.SysConfigConstants;
import com.site.blog.constants.UploadConstants;
import com.site.blog.dto.Result;
import com.site.blog.entity.BlogConfig;
import com.site.blog.service.BlogConfigService;
import com.site.blog.util.MyBlogUtils;
import com.site.blog.util.ResultGenerator;
import com.site.blog.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Description: 上传图片Controller
 * @date: 2019/8/6 21:26
 */
@Controller
@RequestMapping("/admin")
public class UploadController {
    @Value("${uploadFile.resourceHandler}")
    private String resourceHandler;//请求 url 中的资源映射，不推荐写死在代码中，最好提供可配置，如 /uploadFiles/**

    @Value("${uploadFile.location}")
    private String location;//上传文件保存的本地目录，使用@Value获取全局配置文件中配置的属性值，如 E:/wmx/uploadFiles/

    @Value("${uploadFile.linuxlocation}")
    private String linuxlocation;//上传文件保存的linux目录，使用@Value获取全局配置文件中配置的属性值，如 /root/
    @Autowired
    private BlogConfigService blogConfigService;

    /**
     * @Description: 用户头像上传
     * @Param: [httpServletRequest, file]
     * @return: com.zhulin.blog.util.Result
     * @date: 2019/8/24 15:15
     */
    @PostMapping({"/upload/authorImg"})
    @ResponseBody
    public Result upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws URISyntaxException, FileNotFoundException {
        String suffixName = UploadFileUtils.getSuffixName(file);
        //生成文件名称通用方法
        String newFileName = UploadFileUtils.getNewFileName(suffixName);
        String staruri=location;
        File locationfold=new File(location);
        if (!locationfold.exists() && !locationfold.isDirectory()) {
             //判断环境是windows还是linux，确认实际路径
            locationfold=new File(linuxlocation);
            staruri=linuxlocation;
        }




        // 构建上传文件的存放 "文件夹" 路径
        String savePath = staruri+UploadConstants.UPLOAD_AUTHOR_IMG;

        File fileDirectory = new File(savePath);
        try {
            if (!fileDirectory.exists() && !fileDirectory.isDirectory()) {
                System.out.println(fileDirectory + "目录不存在，需要创建");
                //创建目录
                fileDirectory.mkdir();
            }
            //创建文件
            String savedestFilePath = savePath+"/" + newFileName;
            File destFile = new File(savedestFilePath);

            file.transferTo(destFile);
            String sysAuthorImg = MyBlogUtils.getHost(new URI(request.getRequestURL() + ""))
                    + UploadConstants.SQL_AUTHOR_IMG + newFileName;
            BlogConfig blogConfig = new BlogConfig()
                    .setConfigField(SysConfigConstants.SYS_AUTHOR_IMG.getConfigField())
                    .setConfigValue(sysAuthorImg);
            blogConfigService.updateById(blogConfig);
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

}
