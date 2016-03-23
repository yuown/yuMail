package yuown.bulk.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.model.EmailRequest;
import yuown.bulk.service.MailSenderHelper;

@RestController
@RequestMapping(value = "/rest/sendMail", produces = { MediaType.APPLICATION_JSON_VALUE })
public class MailResourceImpl {

    @Autowired
    private MailSenderHelper mailSenderHelper;

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity save(@RequestBody EmailRequest request) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus = null;
        try {
            mailSenderHelper.sendMail(request);
            responseStatus = HttpStatus.OK;
        } catch (Exception e) {
            responseStatus = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        }
        return new ResponseEntity(headers, responseStatus);
    }
}
