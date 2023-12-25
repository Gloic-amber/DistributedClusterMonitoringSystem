package org.ustc.server.controller;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Report;
import org.ustc.server.netty.MyServerHandler;
import org.ustc.server.service.RecordService;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ClientController
 * Package: org.ustc.server.controller
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Slf4j
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClientController {

    @Resource
    private RecordService recordService;

    @Resource
    private MyServerHandler myServerHandler;


    // 修改信息的更新频率
    @PostMapping("/updateFrequency")
    public ResponseEntity<Void> updateFrequency(@RequestParam String clientName, @RequestParam String frequency){
        myServerHandler.updateFrequency(clientName, frequency);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/clients")
    @ResponseBody
    public String getClientsList() {
        return JSON.toJSONString(recordService.clients.getClients().values());
    }

    @PostMapping("/saveClient")
    public void saveClient(@RequestBody Client client){
        client.setOnline(true);
        recordService.clients.getClients().put(client.getName(), client);
        //TODO: 开辟新线程启动一个新的客户端
    }

    @PostMapping("/editClient")
    public void editClient(@RequestBody Client client){
        String name = client.getName();
        recordService.clients.getClients().get(name).setOnline(false);
    }

    @PostMapping("/deleteClient")
    public void deleteClient(@RequestBody Client client){
        String name = client.getName();
        recordService.deleteRecordByName(name);
        recordService.deleteClientByName(name);
    }

    @PostMapping("/updateClient")
    public void updateClient(@RequestBody Client client){
        recordService.updateClientByName(client);
    }

    @RequestMapping("/historyClientsReport")
    @ResponseBody
    public String getHistoryClientReport() {
        List<Report> reports = recordService.findAll();
        return JSON.toJSONString(reports);
    }

    @RequestMapping("/realTimeClientsReport")
    @ResponseBody
    public String getRealTimeClientsReport() {
        List<Report> reports = new ArrayList<>();
        for (String key: recordService.clients.getClients().keySet()) {
            if (recordService.clients.getClients().get(key).isOnline())
                reports.addAll(recordService.getRecordByName(recordService.clients.getClients().get(key).getName()));
        }
        return JSON.toJSONString(reports);
    }

}
