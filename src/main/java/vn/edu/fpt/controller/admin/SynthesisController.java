package vn.edu.fpt.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.fpt.command.SynthesisCommand;
import vn.edu.fpt.constant.SystemConstant;
import vn.edu.fpt.dto.StaffSynthesisDto;
import vn.edu.fpt.service.SynthesisService;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/synthesis")
public class SynthesisController {
    private final String prefixPath = "admin/synthesis/";

    @Autowired
    private SynthesisService synthesisService;

    @GetMapping(value = {"", "search"})
    public ModelAndView list(@RequestParam(value = "staff-code", required = false) String staffCode) {
        SynthesisCommand command = new SynthesisCommand();
        if (staffCode != null) {
            if (StringUtils.isEmpty(staffCode)) {
                return new ModelAndView(SystemConstant.REDIRECT_URL.concat(prefixPath));
            }
            StaffSynthesisDto staffSynthesisDto = synthesisService.getStaffSynthesisByCode(staffCode);
            if (staffSynthesisDto != null) {
                command.setStaffSynthesisDtoList(Arrays.asList(staffSynthesisDto));
            }
        } else {
            command.setStaffSynthesisDtoList(synthesisService.getTopStaffSynthesis(null));
        }

        ModelAndView modelAndView = new ModelAndView(prefixPath.concat("list"));
        modelAndView.addObject(SystemConstant.COMMAND, command);
        return modelAndView;
    }

}
