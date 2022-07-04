package cn.com.hellowood.scheduledjob.dao;


import cn.com.hellowood.scheduledjob.model.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobDao {
    ScheduleJob select(@Param("id") long id);

    Integer update(ScheduleJob scheduleJob);

    Integer insert(ScheduleJob scheduleJob);

    Integer delete(Long productId);

    List<ScheduleJob> getAllJob();

    List<ScheduleJob> getAllEnableJob();
}
