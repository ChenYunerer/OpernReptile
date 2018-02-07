package com.opern.reptile.dao;

import com.opern.reptile.model.OpernInfo;
import com.opern.reptile.model.OpernPicInfo;

import java.util.List;

public interface OpernDao {

    /**
     * 插入曲谱数据
     */
    int insertOpernInfos(List<OpernInfo> opernInfoList);

    /**
     * 插入曲谱数据
     */
    int insertOpernInfo(OpernInfo opernInfo);

    /**
     * 查询曲谱数据
     */
    List<OpernInfo> listOpernInfo();

    /**
     * 插入曲谱图片数据
     */
    int insertOpernPicInfos(List<OpernPicInfo> opernPicInfoList);

    /**
     * 插入曲谱图片数据
     */
    int insertOpernPicInfo(OpernPicInfo opernPicInfo);

    /**
     * 更新曲谱数量首张图信息
     */
    int updateOpernPicNumFirstPicInfo();
}
