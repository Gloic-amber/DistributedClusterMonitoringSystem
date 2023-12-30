package org.ustc.server.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Clients;
import org.ustc.common.pojo.Report;
import org.ustc.server.mapper.RecordMapper;
import org.ustc.server.service.RecordService;

import java.util.List;

/**
 * ClassName: RecordServiceImpl
 * Package: org.ustc.server.service.impl
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Service
public class RecordServiceImpl implements RecordService {

    @Resource
    private RecordMapper recordMapper;

    @Override
    public List<Client> findAllClients() {
        return recordMapper.findAllClients();
    }

    @Override
    public List<Report> findAll() {
        return recordMapper.findAll();
    }

    @Override
    public void saveRecord(Report record) {
        recordMapper.saveRecord(record);
    }

    @Override
    public void saveClient(Client client) {
        clients.getClients().put(client.getName(), client);
        recordMapper.saveClient(client);
    }

    @Override
    public List<Report> getRecordByName(String name) {
        return recordMapper.getRecordByName(name);
    }

    @Override
    public void deleteRecordByName(String name) {
        clients.getClients().remove(name);
        recordMapper.deleteRecordByName(name);
    }

    @Override
    public void deleteClientByName(String name) {
        recordMapper.deleteClientByName(name);
    }

    @Override
    public void updateClientByName(Client client) {
        recordMapper.updateClientByName(client);
        // 更新内存中的client
        clients.getClients().put(client.getName(), client);
    }
}
