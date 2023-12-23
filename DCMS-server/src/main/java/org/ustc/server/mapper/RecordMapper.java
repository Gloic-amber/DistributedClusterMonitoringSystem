package org.ustc.server.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Report;

import java.util.List;

/**
 * ClassName: RecordMapper
 * Package: org.ustc.server.mapper
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Mapper
public interface RecordMapper {

    @Select("select name, os, cpu_num, ip from record")
    List<Client> findAllClients();

    @Select("select * from record")
    List<Report> findAll();

    @Insert("insert into record(name, `load`, os, time_stamp, cpu_num, ip) values(#{name}, #{load}, #{os}, #{timeStamp}, #{cpuNum}, #{ip})")
    void saveRecord(Report record);

    @Select("select * from record where name = #{name}")
    List<Report> getRecordByName(String name);

    @Delete("delete from record where name = #{name}")
    void deleteRecordByName(String name);
}
