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

import static com.mrserg86.EventsOfSmartContract.JavaKafkaConsumerExample.walletAddresses;

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
        List<BigInteger> addressesForListening = walletAddresses;
        addressesForListening.forEach(System.out::println);

        //Убеждаемся, что соединение работает (скачиваем последний блок блокчейна)
        EthBlockNumber result = web3.ethBlockNumber().sendAsync().get();  //здесь запрашивается НОМЕР последнего блока (метод ethBlockNumber() по дефолту запрашивает последний блок)
        BigInteger latestRealBlockNumber = result.getBlockNumber();
        System.out.println(" The latest Block Number is: " + latestRealBlockNumber);

        for (int i = (latestKnownBlockNumber.intValue() + 1); i <= latestRealBlockNumber.intValue(); i++) {
            List<EthBlock.TransactionResult> txr = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send().getBlock().getTransactions();
            txr.forEach(tx -> {
                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                if(transaction.getTo() == null || transaction.getTo().trim().isEmpty()) {
                System.out.println("Empty block");
                } else
                if(transaction.getTo().equalsIgnoreCase(walletAddress)) {
                    txBingo.add(transaction);
                    System.out.println("From address " + transaction.getFrom() + "  was transaction to our address");
                } else {
                    System.out.println("No any transactions with our address in block ");
                }

            });
            latestKnownBlockNumber = latestRealBlockNumber;
            System.out.println("Finish check block number  " + i);
            //break;

        }

//        //скачиваем блок(последний в блокчейне) и выводим sender of each transaction in the LATEST block:
//        List<EthBlock.TransactionResult> txs = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock().getTransactions();
//        txs.forEach(tx -> {
//            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
//
//            System.out.println("Sender of transaction " + transaction.getTransactionIndex() + " in the LATEST block is " + transaction.getFrom());
//        });
//
//        //Скачиваем конкретный блок в блокчейне
//        List<EthBlock.TransactionResult> txr = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(27120252), true).send().getBlock().getTransactions();
//
//        //и проверяем, есть ли в нём транзакции, в которых есть наш адрес, если есть - ложим их в список
//        List<EthBlock.TransactionObject> txBingo = new ArrayList<>();
//        txr.forEach(tx -> {
//            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
//            if (transaction.getFrom().equalsIgnoreCase("0xeDc5c0029309cA5576D452456228ca0B1fE8b9a3")) {
//                txBingo.add(transaction);
//                System.out.println("To address " + transaction.getTo() + " was transaction from our address");
//            }
//            if(transaction.getTo().equalsIgnoreCase("0xeDc5c0029309cA5576D452456228ca0B1fE8b9a3")) {
//                txBingo.add(transaction);
//                System.out.println("From address " + transaction.getFrom() + "  was transaction to our address");
//            } else {
//                System.out.println("No any transactions with our address");
//                //System.out.println("Sender/receiver of transaction " + transaction.getTransactionIndex() + " in the this block is " + transaction.getFrom());
//            }
//
//        });






//        txr.forEach(tx -> {
//            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
//
//            System.out.println(transaction.getFrom());
//        });








//            String wssUrl = "https://data-seed-prebsc-1-s1.binance.org:8545/";
//            Web3j web3 = Web3j.build(new HttpService((wssUrl)));
//
//            // поиск по событиям(event). А нам нужен поиск по нативным
//              EthFilter filt = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, "00xeDc5c0029309cA5576D452456228ca0B1fE8b9a3");
//              web3.ethLogFlowable(filt).subscribe(log -> {
//                System.out.println(log);
//            }, error -> {
//                System.out.println("Error: " + error);
//            });
//
//            // Event definition
//            final Event MY_EVENT = new Event("MyEvent", Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
//            }, new TypeReference<Bytes32>(true) {
//            }, new TypeReference<Uint8>(false) {
//            }));
//
//// Event definition hash
//            final String MY_EVENT_HASH = EventEncoder.encode(MY_EVENT);
//
//// Filter
//            EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, "https://data-seed-prebsc-1-s1.binance.org:8545/");

// Pull all the events for this contract
//            JsonRpc2_0Web3j web3j = new JsonRpc2_0Web3j();
//            web3j.ethLogFlowable(filter).subscribe(log -> {
//                String eventHash = log.getTopics().get(0); // Index 0 is the event definition hash
//
//                if (eventHash.equals(MY_EVENT_HASH)) { // Only MyEvent. You can also use filter.addSingleTopic(MY_EVENT_HASH)
//                    // address indexed _arg1
//                    Address arg1 = (Address) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1), new TypeReference<Address>() {
//                    });
//                    // bytes32 indexed _arg2
//                    Bytes32 arg2 = (Bytes32) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(2), new TypeReference<Bytes32>() {
//                    });
//                    // uint8 _arg3
//                    Uint8 arg3 = (Uint8) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(3), new TypeReference<Uint8>() {
//                    });
//                }
//
//            });


        }

}
