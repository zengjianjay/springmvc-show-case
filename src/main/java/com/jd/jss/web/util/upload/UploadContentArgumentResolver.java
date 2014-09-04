package com.jd.jss.web.util.upload;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * @author 刘兆敏
 * @since 2014-09-03
 */
public class UploadContentArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();

        return UploadContent.class.isAssignableFrom(paramType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String encoding = request.getCharacterEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
            request.setCharacterEncoding(encoding);
        }

        UploadContent content = new UploadContent();

        int boundaryLen = parseBoundary(request);
        int contentLen = request.getContentLength();

        int sum = boundaryLen + 2 + 2 + 2;  // 除去file二进制流外总得长度，初始值为最后一行长度，boundary长度，外加2次回车换行，2 dash

        ServletFileUpload upload = new ServletFileUpload();
        upload.setHeaderEncoding(encoding);
        FileItemIterator iter = null;
        try {
            iter = upload.getItemIterator(request);

            Map<String, Vector<String>> kvs = new HashMap<String, Vector<String>>();

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                FileItemHeaders headers = item.getHeaders();

                sum += boundaryLen + 2; // \r\n

                Iterator i = headers.getHeaderNames();
                while (i.hasNext()) {
                    String headerName = (String) i.next();
                    int headerLen = headerName.length() + 2; // key + ":" + " "

                    sum += headerLen;

                    String header = headers.getHeader(headerName);
                    int headerVLen = header.length() + 2;   // value + "\r\n"

                    sum += headerVLen;
                }

                sum += 2;   // \r\n

                if (item.isFormField()) {   // 表单域
                    String formV = Streams.asString(stream, encoding);
                    sum += formV.length() + 2;

                    Vector<String> vs = kvs.get(name);
                    if (vs == null) {
                        vs = new Vector<String>(5);
                        kvs.put(name, vs);
                    }

                    vs.add(formV);

                } else {    // 文件域 空文件也会上传
                    if (!StringUtils.isEmpty(content.getFilename())) {
                        content.setInput(stream);
                        content.setContentType(item.getContentType());
                        content.setFilename(item.getName());
                    }
                    break;  // 默认只有一个文件域，否则根据http协议表单文件上传，多文件的情况下是不能计算文件大小的
                }
            }

            if (null == content.getInput()) {
                throw new RuntimeException("Not multipart request!");
            }

            if(kvs.size() > 0) {
                Map<String, String[]> params = new HashMap<String, String[]>();
                for (String k : kvs.keySet()) {
                    String[] tmp = new String[kvs.get(k).size()];
                    params.put(k, kvs.get(k).toArray(tmp));
                }
                content.setKvs(params);
            }

            content.setSize(contentLen - sum);
        } catch (FileUploadException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return content;
    }

    private int parseBoundary(HttpServletRequest request) {
        String contentType = request.getContentType();
        String[] contents = contentType.split(";");
        if (contents.length < 2) {
            return 0;
        }
        return contents[1].trim().length() - 9 + 2;
    }
}
