package vn.edu.fpt.service.extend.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.common.paging.PageRequest;
import vn.edu.fpt.common.paging.Pageable;
import vn.edu.fpt.dao.extend.StaffDao;
import vn.edu.fpt.dao.generic.ActiveEntityDao;
import vn.edu.fpt.dto.StaffDto;
import vn.edu.fpt.dto.StaffLiveSearchDto;
import vn.edu.fpt.entity.StaffEntity;
import vn.edu.fpt.mapper.AbstractMapper;
import vn.edu.fpt.mapper.StaffMapper;
import vn.edu.fpt.service.extend.StaffService;
import vn.edu.fpt.service.generic.impl.ActiveEntityServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StaffServiceImpl extends ActiveEntityServiceImpl<Integer, StaffDto> implements StaffService {

    private StaffMapper mapper;
    private StaffDao staffDao;

    @Autowired
    public StaffServiceImpl(@Qualifier("staffDaoImpl") ActiveEntityDao activeEntityDao,
                            @Qualifier("staffMapper") AbstractMapper abstractMapper) {
        super(activeEntityDao, abstractMapper);
        this.mapper = (StaffMapper) abstractMapper;
        this.staffDao = (StaffDao) activeEntityDao;
    }

    @Override
    public StaffDto findByCode(String code) {
        StaffEntity entity = staffDao.findByCode(code);
        if (entity != null)
            return mapper.entityToDto(entity);
        return null;
    }

    @Override
    public List<StaffDto> findAllByName(String name) {
        List<StaffDto> dtoList = new ArrayList<>();
        staffDao.findAllByName(name).forEach(staffEntity -> dtoList.add(mapper.entityToDto(staffEntity)));
        return dtoList;
    }

    @Override
    public List<StaffLiveSearchDto> findAllNameByCodeInLiveSearch(String staffCode) {
        List<StaffLiveSearchDto> dtoList = new ArrayList<>();
        Pageable pageable = new PageRequest(1, 5, null);
        staffDao.findAllByCode(pageable, staffCode).forEach(staffEntity -> {
            StaffLiveSearchDto staffLiveSearchDto = new StaffLiveSearchDto();
            staffLiveSearchDto.setId(staffEntity.getId());
            staffLiveSearchDto.setCode(staffEntity.getCode());
            staffLiveSearchDto.setName(staffEntity.getName());

            dtoList.add(staffLiveSearchDto);
        });
        return dtoList;
    }

    @Override
    public StaffLiveSearchDto findByIdInLiveSearch(Integer id) {
        StaffEntity entity = staffDao.findById(id);
        StaffLiveSearchDto staffLiveSearchDto = null;
        if (entity != null) {
            staffLiveSearchDto = new StaffLiveSearchDto();
            staffLiveSearchDto.setId(entity.getId());
            staffLiveSearchDto.setCode(entity.getCode());
            staffLiveSearchDto.setName(entity.getName());
            staffLiveSearchDto.setDepartName(entity.getDepartEntity().getName());
        }
        return staffLiveSearchDto;
    }
}