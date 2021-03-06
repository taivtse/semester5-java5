package vn.edu.fpt.dto;

import java.io.Serializable;
import java.util.Date;

public class RecordDto implements Serializable, DtoMarker<Integer> {
    private Integer id;
    private String type;
    private String reason;
    private Date submitDate;
    private StaffDto staffDto;
//    staffDepartName auto mapping by ObjectMapper
    private String staffDepartName;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public StaffDto getStaffDto() {
        return staffDto;
    }

    public void setStaffDto(StaffDto staffDto) {
        this.staffDto = staffDto;
    }

    public String getStaffDepartName() {
        return staffDepartName;
    }

    public void setStaffDepartName(String staffDepartName) {
        this.staffDepartName = staffDepartName;
    }

}
