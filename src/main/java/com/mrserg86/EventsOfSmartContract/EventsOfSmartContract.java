package com.mrserg86.EventsOfSmartContract;

import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import java.io.IOException;

@Slf4j
public class EventsOfSmartContract {

    public static void main(String[] args) throws IOException {
//
//        String RPC_URL = "https://data-seed-prebsc-1-s1.binance.org:8545/";
//        Web3j web3 = Web3j.build(new HttpService((RPC_URL)));
//        log.info(web3.web3ClientVersion().send().getWeb3ClientVersion());
//
//    }

//    public void parsingOfEvents() throws IOException {
        String wssUrl = "https://data-seed-prebsc-1-s1.binance.org:8545/";
        Web3j web3 = Web3j.build(new HttpService((wssUrl)));

        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, "https://data-seed-prebsc-1-s1.binance.org:8545/");
//        web3.ethLogFlowable(filter).subscribe(log -> {
//            System.out.println(log);
//        });
//        web3.ethGetLogs(filter);
        System.out.println(web3.ethGetLogs(filter));
    }

}
