package com.xtu.graduate.project.service.impl;


import com.xtu.graduate.project.dao.CommonDao;
import com.xtu.graduate.project.dao.DepartmentDao;
import com.xtu.graduate.project.dao.impl.UserDaoImpl;
import com.xtu.graduate.project.domains.CurrentPage;
import com.xtu.graduate.project.domains.SiteApplication;
import com.xtu.graduate.project.domains.SiteInfo;
import com.xtu.graduate.project.service.CommonService;
import com.xtu.graduate.project.service.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/1 0001.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);
    @Autowired
    DepartmentDao departmentDao;

    @Autowired
    CommonDao commonDao;

    @Override
    public CurrentPage findSiteApplicationInfo(String departmentID, String locale, Date beginTime1, Date beginTime2, int pageNumber) {
        LOGGER.info("Finding Site Application, departmentID = {}, locale = {}, begainTime1 = {}, begainTime2 = {}, " +
                "pageNumber = {}", departmentID, locale, beginTime1, beginTime2, pageNumber);
        CurrentPage page;
        if (StringUtils.isNotBlank(departmentID)) {
            page = this.departmentDao.findSiteApplicationByDepartmentID(departmentID, pageNumber);
            return page;
        }
        if (StringUtils.isNotBlank(locale)) {
            if(beginTime1 != null && beginTime2 != null) {
                page = this.departmentDao.findSiteApplicationByLocaleAndBeginTime(locale, beginTime1, beginTime2, pageNumber);
                return page;
            }
            page = this.departmentDao.findSiteApplicationByLocale(locale, pageNumber);
            return page;
        }
        if (beginTime1 != null && beginTime2 != null) {
            page = this.departmentDao.findSiteApplicationByBeginTime(beginTime1, beginTime2, pageNumber);
            return page;
        }
        page = this.departmentDao.findAllSiteApplication(pageNumber);
        return page;
    }

    @Override
    public Integer createSiteApplication(SiteApplication siteApplication, String siteName) {
        LOGGER.info("Creating site application, siteName = {}", siteName);
        SiteInfo siteInfo = this.commonDao.findSiteInfoBySiteName(siteName);
        if (siteInfo == null) {
            LOGGER.info("场地信息不存在");
            return 0;
        }
        String siteID = siteInfo.getSiteID();
        int rows = this.commonDao.whetherHasApproved(siteID, siteApplication.getBeginTime());
        if (rows == 1) {
            LOGGER.info("该时间段的场地已被申请");
            return -1;
        }
        String siteManagerID = siteInfo.getSiteManagerID();
        siteApplication.setSiteID(siteID);
        siteApplication.setSiteManagerID(siteManagerID);
        return this.departmentDao.createSiteApplication(siteApplication);
    }
}
