package org.ustc.server.mapper;

import org.apache.ibatis.annotations.*;
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
public interface RecordMapper{

    @Select("select name, os, cpu_num, ip from client")
    List<Client> findAllClients();

    @Select("select * from record")
    List<Report> findAll();

    @Insert("insert into record(name, `load`, time_stamp) values(#{name}, #{load}, #{timeStamp})")
    void saveRecord(Report record);

    @Insert("insert into client(name, os, cpu_num, ip) values (#{name}, #{os}, #{cpuNum}, #{ip})")
    void saveClient(Client client);

    @Select("select * from record_view where name = #{name}")
    List<Report> getRecordByName(String name);

    @Delete("delete from record where name = #{name}")
    void deleteRecordByName(String name);

    @Delete("delete from client where name = #{name}")
    void deleteClientByName(String name);

    @Update("UPDATE client SET os = #{os}, cpu_num = #{cpuNum}, ip = #{ip} WHERE name = #{name}")
    void updateClientByName(Client client);
}
