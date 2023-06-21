package org.nastation.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.nastation.module.address.data.Address;
import org.nastation.module.address.repo.AddressRepository;
import org.nastation.module.appstore.data.AppStoreItem;
import org.nastation.module.appstore.repo.AppStoreItemRepository;
import org.nastation.module.dapp.data.DApp;
import org.nastation.module.dapp.repo.DAppRepository;
import org.nastation.module.dfs.data.FileItem;
import org.nastation.module.dfs.repo.FileItemRepository;
import org.nastation.module.dns.data.Domain;
import org.nastation.module.dns.data.DomainDnsItem;
import org.nastation.module.dns.repo.DomainDnsitemRepository;
import org.nastation.module.dns.repo.DomainRepository;
import org.nastation.module.vote.data.Vote;
import org.nastation.module.vote.repo.VoteRepository;
import org.nastation.module.wallet.data.Wallet;
import org.nastation.module.wallet.repo.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

import java.time.LocalDateTime;

@SpringComponent
public class DataGenerator {

    public final DataType<Long> LongID = new LongIdDataType();
    public final DataType<Double> DoubleID = new DoubleIdDataType();

    @Bean
    public CommandLineRunner loadData(
            DAppRepository dAppRepository,
            AddressRepository addressRepository,
            VoteRepository voteRepository,
            DomainRepository domainRepository,
            DomainDnsitemRepository domainDnsitemRepository,
            WalletRepository walletRepository,
            AppStoreItemRepository appStoreItemRepository,
            FileItemRepository fileItemRepository
    ) {

        return args -> {

            if (false) { // true false

                Logger logger = LoggerFactory.getLogger(getClass());
                int seed = 123;

                logger.info("Generating demo data");

                logger.info("... generating 100 Wallet entities...");
                ExampleDataGenerator<Wallet> walletRepositoryGenerator = new ExampleDataGenerator<>(Wallet.class,
                        LocalDateTime.of(2021, 6, 7, 0, 0, 0));
                walletRepositoryGenerator.setData(Wallet::setId, DataType.ID);
                walletRepositoryGenerator.setData(Wallet::setName, DataType.WORD);
                walletRepositoryGenerator.setData(Wallet::setAddress, DataType.WORD);
                walletRepositoryGenerator.setData(Wallet::setPswTip, DataType.WORD);
                walletRepositoryGenerator.setData(Wallet::setAddTime, DataType.DATETIME_LAST_10_YEARS);
                //walletRepositoryGenerator.setData(Wallet::setCreateType, WalletService.CreateTypes.CREATE_VALUE);
                walletRepositoryGenerator.setData(Wallet::setFullNode, DataType.BOOLEAN_50_50);
                walletRepositoryGenerator.setData(Wallet::setDefaultWallet, DataType.BOOLEAN_50_50);
                walletRepositoryGenerator.setData(Wallet::setHasBackup, DataType.BOOLEAN_50_50);
                walletRepositoryGenerator.setData(Wallet::setRemark, DataType.WORD);

                walletRepository.saveAll(walletRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 D App entities...");
                ExampleDataGenerator<DApp> dAppRepositoryGenerator = new ExampleDataGenerator<>(DApp.class,
                        LocalDateTime.of(2021, 5, 24, 0, 0, 0));
                dAppRepositoryGenerator.setData(DApp::setId, DataType.ID);
                dAppRepositoryGenerator.setData(DApp::setName, DataType.FIRST_NAME);
                dAppRepositoryGenerator.setData(DApp::setIcon, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setDomain, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setStorageHash, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setDappHash, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setAuthorAddress, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setType, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setStatus, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setFileSize, DataType.FULL_NAME);
                dAppRepositoryGenerator.setData(DApp::setAddTime, DataType.DATETIME_LAST_10_YEARS);
                dAppRepository.saveAll(dAppRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 Address entities...");
                ExampleDataGenerator<Address> addressRepositoryGenerator = new ExampleDataGenerator<>(Address.class,
                        LocalDateTime.of(2021, 5, 24, 0, 0, 0));
                addressRepositoryGenerator.setData(Address::setId, DataType.ID);
                addressRepositoryGenerator.setData(Address::setAddress, DataType.FIRST_NAME);
                addressRepositoryGenerator.setData(Address::setLabel, DataType.FULL_NAME);
                addressRepositoryGenerator.setData(Address::setRemark, DataType.FULL_NAME);
                addressRepositoryGenerator.setData(Address::setTokenId, LongID);
                addressRepository.saveAll(addressRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 Vote entities...");
                ExampleDataGenerator<Vote> voteRepositoryGenerator = new ExampleDataGenerator<>(Vote.class,
                        LocalDateTime.of(2021, 5, 24, 0, 0, 0));
                voteRepositoryGenerator.setData(Vote::setId, DataType.ID);
                voteRepositoryGenerator.setData(Vote::setVoteAddress, DataType.FIRST_NAME);
                voteRepositoryGenerator.setData(Vote::setAmount, DoubleID);
                voteRepositoryGenerator.setData(Vote::setStatus, DataType.NUMBER_UP_TO_100);
                voteRepositoryGenerator.setData(Vote::setTxHash, DataType.FIRST_NAME);
                voteRepositoryGenerator.setData(Vote::setVoteTime, DataType.DATETIME_LAST_10_YEARS);
                voteRepository.saveAll(voteRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 Domain entities...");
                ExampleDataGenerator<Domain> domainRepositoryGenerator = new ExampleDataGenerator<>(Domain.class,
                        LocalDateTime.of(2021, 9, 2, 0, 0, 0));
                domainRepositoryGenerator.setData(Domain::setId, DataType.ID);
                domainRepositoryGenerator.setData(Domain::setName, DataType.FIRST_NAME);
                domainRepositoryGenerator.setData(Domain::setRegDate, DataType.DATETIME_LAST_10_YEARS);
                domainRepositoryGenerator.setData(Domain::setRegBlock, DataType.NUMBER_UP_TO_100);
                domainRepositoryGenerator.setData(Domain::setExpireDate, DataType.DATETIME_LAST_10_YEARS);
                domainRepositoryGenerator.setData(Domain::setPaymentAmount, DoubleID);
                domainRepositoryGenerator.setData(Domain::setPaymentCoinTypeId, DataType.ID);
                domainRepositoryGenerator.setData(Domain::setPaymentCoinType, DataType.WORD);
                domainRepositoryGenerator.setData(Domain::setNameserver1, DataType.WORD);
                domainRepositoryGenerator.setData(Domain::setNameserver2, DataType.WORD);
                domainRepositoryGenerator.setData(Domain::setNameserver3, DataType.WORD);
                domainRepositoryGenerator.setData(Domain::setTxhash, DataType.WORD);
                domainRepositoryGenerator.setData(Domain::setAccountAddress, DataType.WORD);
                domainRepository.saveAll(domainRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 Domain Dnsitem entities...");
                ExampleDataGenerator<DomainDnsItem> domainDnsitemRepositoryGenerator = new ExampleDataGenerator<>(
                        DomainDnsItem.class, LocalDateTime.of(2021, 9, 2, 0, 0, 0));
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setId, DataType.ID);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setDomainId, DataType.NUMBER_UP_TO_100);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setHost, DataType.WORD);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setRecordType, DataType.WORD);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setAddress, DataType.WORD);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setPriority, DataType.NUMBER_UP_TO_100);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setEnable, DataType.BOOLEAN_50_50);
                domainDnsitemRepositoryGenerator.setData(DomainDnsItem::setAccountAddress, DataType.WORD);
                domainDnsitemRepository.saveAll(domainDnsitemRepositoryGenerator.create(10, seed));

                logger.info("... generating 100 File Item entities...");
                ExampleDataGenerator<FileItem> fileItemRepositoryGenerator = new ExampleDataGenerator<>(FileItem.class,
                        LocalDateTime.of(2021, 9, 15, 0, 0, 0));
                fileItemRepositoryGenerator.setData(FileItem::setId, DataType.ID);
                fileItemRepositoryGenerator.setData(FileItem::setFileName, DataType.WORD);
                fileItemRepositoryGenerator.setData(FileItem::setFileSize, DataType.WORD);
                fileItemRepositoryGenerator.setData(FileItem::setFileType, DataType.STATE);
                fileItemRepositoryGenerator.setData(FileItem::setBucketName, DataType.WORD);
                fileItemRepositoryGenerator.setData(FileItem::setFileHash, DataType.WORD);
                fileItemRepositoryGenerator.setData(FileItem::setAuthorAddress, DataType.WORD);
                fileItemRepositoryGenerator.setData(FileItem::setAddTime, DataType.DATETIME_LAST_1_YEAR);
                fileItemRepositoryGenerator.setData(FileItem::setFee, DataType.WORD);
                fileItemRepository.saveAll(fileItemRepositoryGenerator.create(10, seed));

                init_AppStoreItem(appStoreItemRepository);

                logger.info("Generated demo data");
            }

        };
    }

    private void init_AppStoreItem(AppStoreItemRepository appStoreItemRepository) {

        appStoreItemRepository.deleteAll();

        for (int i = 0; i < 8; i++) {
            AppStoreItem one = new AppStoreItem();
            one.setId(i + 1);
            one.setName("Blog" + i);
            one.setImage("Blog" + i);
            one.setIntro("Blog" + i);
            one.setAuthor("Boomerang allows you to schedule messages to be sent or returned at a later date. Write a message now, send it whenever, even if you're not online. Track messages to make sure you hear back, and schedule reminders right inside Gmail™. Know whether your email got read with cross-platform read receipts. Free yourself from constant interruptions using Inbox Pause, while still getting notified about the emails that matter." + i);
            one.setVersionText("1.0." + i);
            one.setCategory("Tool");
            one.setSize("1" + i + " MB");
            one.setAuthor("Na" + i);
            one.setHash("N6364832435345675676" + i);
            appStoreItemRepository.save(one);
        }

    }

}