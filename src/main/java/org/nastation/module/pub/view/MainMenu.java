package org.nastation.module.pub.view;

import org.nastation.module.address.view.AddressListView;
import org.nastation.module.appstore.view.AppStoreView;
import org.nastation.module.dapp.view.PublishDAppListView;
import org.nastation.module.dapp.view.UploadDAppFormView;
import org.nastation.module.devstore.view.DevStoreView;
import org.nastation.module.dfs.view.BuySpaceFormView;
import org.nastation.module.dfs.view.PinFileItemListView;
import org.nastation.module.dfs.view.TempFileItemListView;
import org.nastation.module.dfs.view.TempSpaceFileUploadFormView;
import org.nastation.module.dns.view.DomainApplyFormView;
import org.nastation.module.dns.view.DomainRentFormView;
import org.nastation.module.dns.view.MyApplyDomainListView;
import org.nastation.module.dns.view.MyRentDomainListView;
import org.nastation.module.fullnode.view.FullNodeListView;
import org.nastation.module.fullnode.view.RedeemFullNodeForm;
import org.nastation.module.nft.view.*;
import org.nastation.module.token.view.DeployTokenView;
import org.nastation.module.vote.view.MyVoteListView;
import org.nastation.module.vote.view.VoteFormView;
import org.nastation.module.vote.view.VoteNodeListView;
import org.nastation.module.wallet.view.*;
import org.vaadin.addons.apollonav.ApolloNav;
import org.vaadin.addons.apollonav.ApolloNavItem;

import java.util.Arrays;

public class MainMenu {

    public static ApolloNav getApolloNav() {

        ApolloNav nav = new ApolloNav();
        //nav.setLabel("Main menu");
        nav.setItems(
                new ApolloNavItem("", "Home", "home"),

                //new ApolloNavItem(CardListView.Route_Value, CardListView.Page_Title, "option"),

                new ApolloNavItem(DashboardView.Route_Value, "Overview", "cluster", null, Arrays.asList(
                        new ApolloNavItem(DashboardView.Route_Value, "Dashboard", "angleRight"),
                        new ApolloNavItem(DataSyncView.Route_Value, "Data Sync", "angleRight"),
                        new ApolloNavItem(InstanceListView.Route_Value, "Instance List", "angleRight"),
                        new ApolloNavItem(TokenListView.Route_Value, "Token List", "angleRight"),
                        new ApolloNavItem(PromotionListView.Route_Value, "Promotion List", "angleRight")
                )),

                new ApolloNavItem(WalletListView.Route_Value, "Wallet Module", "wallet", null, Arrays.asList(
                        new ApolloNavItem(CreateWalletFormView.Route_Value, "Create", "angleRight"),
                        new ApolloNavItem(TransferFormView.Route_Value, TransferFormView.Page_Title, "angleRight"),
                        new ApolloNavItem(ReceiveFormView.Route_Value, ReceiveFormView.Page_Title, "angleRight"),
                        new ApolloNavItem(CrossInstanceTransferFormView.Route_Value, "Cross", "angleRight"),
                        new ApolloNavItem(AddressListView.Route_Value, "Address", "angleRight"),
                        new ApolloNavItem(BroadcastTxView.Route_Value, "Broadcast", "angleRight")
                        //new ApolloNavItem(SwapFormView.Route_Value, SwapFormView.Page_Title, "angleRight"),
                        //new ApolloNavItem(WalletProfileFormView.Route_Value, WalletProfileFormView.Page_Title, "angleRight"),
                        //new ApolloNavItem(TransferListView.Route_Value, TransferListView.Page_Title, "angleRight"),
                        //new ApolloNavItem(BlockDataListView.Route_Value, "Block List", "angleRight"),
                        //new ApolloNavItem(TxDataListView.Route_Value, "Transaction List", "angleRight")
                )),

                new ApolloNavItem(NftCollectionListView.Route_Value, "NFT Module", "cubes", null, Arrays.asList(
                        new ApolloNavItem(NftCollectionListView.Route_Value, "Collections", "angleRight"),
                        new ApolloNavItem(NftItemListView.Route_Value, "NFT List", "angleRight"),
                        new ApolloNavItem(DeployNftView.Route_Value, DeployNftView.Page_Title, "angleRight"),
                        new ApolloNavItem(MintNftView.Route_Value, MintNftView.Page_Title, "angleRight"),
                        new ApolloNavItem(TransferNftView.Route_Value, TransferNftView.Page_Title, "angleRight")
                )),

                new ApolloNavItem(RedeemFullNodeForm.Route_Value, "FullNode Module", "cluster", null, Arrays.asList(
                        new ApolloNavItem(RedeemFullNodeForm.Route_Value, "Redeem", "angleRight"),
                        new ApolloNavItem(FullNodeListView.Route_Value, "FullNode List", "angleRight")
                )),


                new ApolloNavItem(MyVoteListView.Route_Value, "Vote Module", "handshake", null, Arrays.asList(
                        new ApolloNavItem(VoteFormView.Route_Value, VoteFormView.Page_Title, "angleRight"),
                        new ApolloNavItem(MyVoteListView.Route_Value, "My Voting", "angleRight"),
                        new ApolloNavItem(VoteNodeListView.Route_Value, "Voting List", "angleRight")
                )),

                new ApolloNavItem(PinFileItemListView.Route_Value, "DFS Module", "file", null, Arrays.asList(
                        new ApolloNavItem(BuySpaceFormView.Route_Value, "Buy Space", "angleRight"),
                        new ApolloNavItem(TempSpaceFileUploadFormView.Route_Value, "File Upload", "angleRight"),
                        new ApolloNavItem(TempFileItemListView.Route_Value, "Temp Files", "angleRight"),
                        new ApolloNavItem(PinFileItemListView.Route_Value, "Pin Files", "angleRight")
                )),

                new ApolloNavItem(PublishDAppListView.Route_Value, "DApp Module", "cubes", null, Arrays.asList(
                        new ApolloNavItem(DeployTokenView.Route_Value, DeployTokenView.Page_Title, "angleRight"),
                        new ApolloNavItem(UploadDAppFormView.Route_Value, UploadDAppFormView.Page_Title, "angleRight")
                        //new ApolloNavItem(DeployDAppFormView.Route_Value, DeployDAppFormView.Page_Title, "angleRight")
                )),

                new ApolloNavItem(MyRentDomainListView.Route_Value, "DNS Module", "globe", null, Arrays.asList(
                        new ApolloNavItem(DomainRentFormView.Route_Value, "Rent", "angleRight"),
                        new ApolloNavItem(MyRentDomainListView.Route_Value, "Rent List", "angleRight"),
                        new ApolloNavItem(DomainApplyFormView.Route_Value, "Apply", "angleRight"),
                        new ApolloNavItem(MyApplyDomainListView.Route_Value, "Apply List", "angleRight")
                )),

                //new ApolloNavItem(NodeMapView.Route_Value, "Node Module", "cluster", null, Arrays.asList(
                //        new ApolloNavItem(NodeListView.Route_Value, NodeListView.Page_Title, "angleRight"),
                //        new ApolloNavItem(ApplyFullNodeFormView.Route_Value, ApplyFullNodeFormView.Page_Title, "angleRight")
                //)),

                new ApolloNavItem(AppStoreView.Route_Value, AppStoreView.Page_Title, "cart"),
                new ApolloNavItem(DevStoreView.Route_Value, DevStoreView.Page_Title, "laptop"),
                new ApolloNavItem(ChangelogView.Route_Value, ChangelogView.Page_Title, "asterisk"),
                new ApolloNavItem(SettingsView.Route_Value, SettingsView.Page_Title, "cogs"),
                new ApolloNavItem(AppAboutView.Route_Value, AppAboutView.Page_Title, "package")


                //new ApolloNavItem(AppStoreView.Route_Value, AppStoreView.Page_Title, "asterisk", null, Arrays.asList(
                //        //new ApolloNavItem(AppStoreDetailView.Route_Value + "/81", AppStoreDetailView.Page_Title, "angleRight")
                //)),
                //new ApolloNavItem(DevStoreView.Route_Value, DevStoreView.Page_Title, "laptop", null, Arrays.asList(
                //))
                //,

                //new ApolloNavItem(NppView.Route_Value, NppView.Page_Title, "records", null, Arrays.asList(
                //        new ApolloNavItem(EditorView.Route_Value, EditorView.Page_Title, "angleRight")
                //))
                //,

                //new ApolloNavItem(RestApiView.Route_Value, RestApiView.Page_Title, "code", null, Arrays.asList(
                //        new ApolloNavItem(WsApiView.Route_Value, WsApiView.Page_Title, "angleRight")
                //))
                //,

                //new ApolloNavItem(MiningFormView.Route_Value, "Mining Module", "bolt", null, Arrays.asList(
                //        new ApolloNavItem(DataStoreSelectFormView.Route_Value, DataStoreSelectFormView.Page_Title, "angleRight"),
                //        new ApolloNavItem(MiningFormView.Route_Value, MiningFormView.Page_Title, "angleRight")
                //)),


                //new ApolloNavItem(UnlockWalletFormView.Route_Value, "Lock Screen", "lock"),

        );

        return nav;
    }

}
