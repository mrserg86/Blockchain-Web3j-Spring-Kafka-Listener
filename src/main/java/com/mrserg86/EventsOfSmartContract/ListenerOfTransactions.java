package com.mrserg86.EventsOfSmartContract;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.mrserg86.EventsOfSmartContract.JavaKafkaConsumerExample.consume;

//import static com.mrserg86.EventsOfSmartContract.JavaKafkaConsumerExample.walletAddresses;

@Slf4j
@EnableScheduling
@Service
public class ListenerOfTransactions {

    //Адрес кошелька, который слушаем
    static final String walletAddress = "0xeDc5c0029309cA5576D452456228ca0B1fE8b9a3";

    //Соеднияемся с блокчейном
    static Web3j web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));

    //переменная, в которую записываем номер блока, который проверили последним. В начале инициализируем значением последнего блока с bscscan
    static BigInteger latestKnownBlockNumber = new BigInteger(String.valueOf(27252882)); //27120252

    //Список, в который сохраняем транзакции, в которых есть прослушиваемый кошелёк
    public static List<EthBlock.TransactionObject> txBingo = new ArrayList<>();

    @Scheduled(fixedDelay = 3000)
    public static void listenAddress() throws IOException, ExecutionException, InterruptedException {

        //получаем список адресов для прослушивания из KafkaConsumer
        List<String> addressesForListening = consume();
        addressesForListening.forEach(System.out::println);
        EthBlockNumber result = web3.ethBlockNumber().sendAsync().get();  //здесь запрашивается НОМЕР последнего блока (метод ethBlockNumber() по дефолту запрашивает последний блок)
        BigInteger latestRealBlockNumber = result.getBlockNumber();
        System.out.println(" The latest Block Number is: " + latestRealBlockNumber);

        addressesForListening.forEach(aFL -> {
            for (int i = (latestKnownBlockNumber.intValue() + 1); i <= latestRealBlockNumber.intValue(); i++) {
                List<EthBlock.TransactionResult> txr = null;
                try {
                    txr = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send().getBlock().getTransactions();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                txr.forEach(tx -> {
                    EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                    if(transaction.getTo() == null || transaction.getTo().trim().isEmpty()) {
                        System.out.println("Empty block");
                    } else
                    if(transaction.getTo().equalsIgnoreCase(aFL)) {
                        txBingo.add(transaction);
                        System.out.println("From address " + transaction.getFrom() + "  was transaction to " + aFL + " address");
                    } else {
                        System.out.println("No any transactions with our address in block ");
                    }

                });
                latestKnownBlockNumber = latestRealBlockNumber;
                System.out.println("Finish check block number  " + i);
                //break;

            }
        });

//        //Убеждаемся, что соединение работает (скачиваем последний блок блокчейна)
//        EthBlockNumber result = web3.ethBlockNumber().sendAsync().get();  //здесь запрашивается НОМЕР последнего блока (метод ethBlockNumber() по дефолту запрашивает последний блок)
//        BigInteger latestRealBlockNumber = result.getBlockNumber();
//        System.out.println(" The latest Block Number is: " + latestRealBlockNumber);
//
//        for (int i = (latestKnownBlockNumber.intValue() + 1); i <= latestRealBlockNumber.intValue(); i++) {
//            List<EthBlock.TransactionResult> txr = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send().getBlock().getTransactions();
//            txr.forEach(tx -> {
//                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
//                if(transaction.getTo() == null || transaction.getTo().trim().isEmpty()) {
//                System.out.println("Empty block");
//                } else
//                if(transaction.getTo().equalsIgnoreCase(walletAddress)) {
//                    txBingo.add(transaction);
//                    System.out.println("From address " + transaction.getFrom() + "  was transaction to " + walletAddress + " address");
//                } else {
//                    System.out.println("No any transactions with our address in block ");
//                }
//
//            });
//            latestKnownBlockNumber = latestRealBlockNumber;
//            System.out.println("Finish check block number  " + i);
//            //break;
//
//        }



        }

}
