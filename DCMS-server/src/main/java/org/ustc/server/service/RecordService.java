package org.ustc.server.service;

import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Clients;
import org.ustc.common.pojo.Report;
import org.ustc.server.mapper.RecordMapper;

import java.util.List;

/**
 * ClassName: RecordService
 * Package: org.ustc.server.service
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */


public interface RecordService {

    Clients clients = new Clients();

    List<Client> findAllClients();

    List<Report> findAll();

    void saveRecord(Report record);

    List<Report> getRecordByName(String name);

    void deleteRecordByName(String name);
}
