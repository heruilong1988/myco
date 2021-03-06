package org.hrl.api.huobi.task;

import org.hrl.api.huobi.client.ApiClient;
import org.hrl.api.huobi.response.Account;
import org.hrl.api.huobi.response.ApiResponse;
import org.hrl.api.rsp.MAccount;
import org.hrl.api.rsp.MGetAccountsRsp;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetAccountsTask implements Callable<MGetAccountsRsp> {

    private ApiClient apiClient;

    public GetAccountsTask(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public MGetAccountsRsp call() throws Exception {

        ApiResponse<List<Account>> resp =
                apiClient.get("/v1/account/accounts", null, new TypeReference<ApiResponse<List<Account>>>() {
                });
        List<Account> accounts = resp.checkAndReturn();

        List<MAccount>  mAccounts = new ArrayList<>();
        for(Account account : accounts) {
            MAccount mAccount = new MAccount();
            mAccount.setAccountId(String.valueOf(account.id));
            mAccount.setState(account.state);
            mAccount.setType(account.type);
            mAccounts.add(mAccount);
        }

        MGetAccountsRsp mGetAccountsRsp = new MGetAccountsRsp();
        mGetAccountsRsp.setAccountList(mAccounts);
        return mGetAccountsRsp;
    }


}
