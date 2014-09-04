package org.springframework.samples.mvc.fileupload;

import com.jd.jss.web.util.upload.UploadContent;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/fileupload")
public class FileUploadController {

    @ModelAttribute
    public void ajaxAttribute(WebRequest request, Model model) {
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
    }

    @RequestMapping(method = RequestMethod.GET)
    public void fileUploadForm() {
    }

//	@RequestMapping(method=RequestMethod.POST)
//	public void processUpload(@RequestParam MultipartFile file, Model model) throws IOException {
//		model.addAttribute("message", "File '" + file.getOriginalFilename() + "' uploaded successfully");
//	}

    @RequestMapping(method = RequestMethod.POST)
    public void processUpload(UploadContent content) {
        System.out.println(content);
    }

}
