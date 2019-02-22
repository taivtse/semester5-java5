package vn.edu.fpt.controller.admin;

import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.fpt.command.StaffCommand;
import vn.edu.fpt.constant.SystemConstant;
import vn.edu.fpt.dto.DepartDto;
import vn.edu.fpt.dto.PNotifyDto;
import vn.edu.fpt.dto.StaffDto;
import vn.edu.fpt.dto.StaffLiveSearchDto;
import vn.edu.fpt.service.generic.extend.DepartService;
import vn.edu.fpt.service.generic.extend.StaffService;
import vn.edu.fpt.util.FormUtil;
import vn.edu.fpt.util.ResourceBundleUtil;
import vn.edu.fpt.util.SessionUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = {"/admin/staff"})
public class StaffController {
    private final String prefixPath = "admin/staff/";
    @Autowired
    private StaffService staffService;

    @Autowired
    private DepartService departService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping({"", "search"})
    public ModelAndView list(@RequestParam(value = "name", required = false) String searchName,
                             HttpServletRequest request) {
        StaffCommand command = new StaffCommand();

        if (searchName != null) {
            command.setListResult(staffService.findAllActiveByName(searchName));
        } else {
            command.setListResult(staffService.findAllActive());
        }

        PNotifyDto pNotifyDto = (PNotifyDto) SessionUtil.getInstance().get(request, SystemConstant.PNOTIFY);
        if (pNotifyDto != null) {
            command.setpNotifyDto(pNotifyDto);
            SessionUtil.getInstance().remove(request, SystemConstant.PNOTIFY);
        }

        ModelAndView modelAndView = new ModelAndView(prefixPath.concat("list"));
        modelAndView.addObject(SystemConstant.COMMAND, command);

        return modelAndView;
    }

    @GetMapping({"info/{code}", "info/"})
    public ModelAndView info(@PathVariable(value = "code", required = false) String code) {
        StaffCommand command = new StaffCommand();

        if (code != null) {
            StaffDto staffDto = staffService.findOneActiveByCode(code);
            if (staffDto != null) {
                command.setPojo(staffDto);
            } else {
                return new ModelAndView(SystemConstant.REDIRECT_URL.concat(prefixPath));
            }
        } else {
            command.setPojo(null);
        }

        List<DepartDto> departDtoList = departService.findAllActive();
        command.setDepartDtoList(departDtoList);

        ModelAndView modelAndView = new ModelAndView(prefixPath.concat("edit"));
        modelAndView.addObject(SystemConstant.COMMAND, command);
        return modelAndView;
    }

    @GetMapping("live-search/info/{staffCode}")
    @ResponseBody
    public StaffLiveSearchDto getStaffInfoInLiveSearch(@PathVariable("staffCode") String staffCode) {
        return staffService.findOneActiveByCodeInLiveSearch(staffCode);
    }

    @GetMapping("live-search")
    @ResponseBody
    public List<StaffLiveSearchDto> getStaffsByCodeInLiveSearch(@RequestParam("staffCode") String staffCode) {
        List<StaffLiveSearchDto> liveSearchNameList = staffService.findAllActiveByCodeInLiveSearch(staffCode);
        return liveSearchNameList;
    }

    @PostMapping
    public ModelAndView insertOrUpdate(HttpServletRequest request,
                                       @RequestParam(value = "pojo.photo", required = false) MultipartFile staffPhoto,
                                       Locale locale) {
        StaffCommand command = FormUtil.populate(StaffCommand.class, request);
        command.setLocale(locale);
        PNotifyDto pNotifyDto = new PNotifyDto();
        StaffDto staffDto;
        try {
            command.setStaffPhoto(staffPhoto);
            if (command.getPojo().getId() == null) {
                staffDto = staffService.saveWithActiveStatus(command);
                pNotifyDto.setTitle(messageSource.getMessage("label.insert.success", null, command.getLocale()));
                pNotifyDto.setText(messageSource.getMessage("label.staff.insert.success", null, command.getLocale()));
            } else {
                staffDto = staffService.updateWithActiveStatus(command);
                pNotifyDto.setTitle(messageSource.getMessage("label.update.success", null, command.getLocale()));
                pNotifyDto.setText(messageSource.getMessage("label.staff.update.success", null, command.getLocale()));
            }

            pNotifyDto.setType(SystemConstant.SUCCESS);
            pNotifyDto.setText(String.format(pNotifyDto.getText(), staffDto.getCode(), staffDto.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            pNotifyDto.setTitle(messageSource.getMessage("label.error", null, command.getLocale()));
            pNotifyDto.setText(messageSource.getMessage("label.error.fail", null, command.getLocale()));
            pNotifyDto.setType(SystemConstant.ERROR);
        }
        SessionUtil.getInstance().put(request, SystemConstant.PNOTIFY, pNotifyDto);

        ModelAndView modelAndView = new ModelAndView(SystemConstant.REDIRECT_URL.concat(prefixPath));
        return modelAndView;
    }

    @DeleteMapping("{staffId}")
    @ResponseBody
    public String delete(@PathVariable("staffId") Integer staffId) {
        try {
            staffService.updateToUnActiveById(staffId);
            return ResourceBundleUtil.getCommonBundle().get("label.response.success");
        } catch (StaleStateException e) {
            e.printStackTrace();
            return ResourceBundleUtil.getCommonBundle().get("label.response.primary_key");
        } catch (Exception e) {
            e.printStackTrace();
            return ResourceBundleUtil.getCommonBundle().get("label.response.error");
        }
    }
}
