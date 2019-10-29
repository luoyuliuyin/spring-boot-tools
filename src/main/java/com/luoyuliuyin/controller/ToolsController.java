package com.luoyuliuyin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by jingfeng on 16-6-15.
 * demo测试
 */

@Controller
public class ToolsController {

    private static final Logger logger = LoggerFactory.getLogger(ToolsController.class);

    @RequestMapping("/")
    private String tools(HttpServletRequest request, ModelMap modelMap) {
        File file = new File(".");
        List<String> fileNameList = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> f.isFile() && !f.getName().startsWith(".")).forEach(f -> fileNameList.add(f.getName()));
        modelMap.put("files", fileNameList);
        return "tools.ftl";
    }

    @ResponseBody
    @RequestMapping("download")
    private void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("file");
        File file = new File(fileName);
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
