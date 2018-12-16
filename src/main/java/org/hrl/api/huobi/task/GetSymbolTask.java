package org.hrl.api.huobi.task;

import org.hrl.api.huobi.client.ApiClient;
import org.hrl.api.huobi.response.ApiResponse;
import org.hrl.api.huobi.response.Symbol;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.Callable;

public class GetSymbolTask implements Callable<List<Symbol>> {

    public ApiClient apiClient;

    public GetSymbolTask(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Symbol> call() throws Exception {
        ApiResponse<List<Symbol>> resp =
                apiClient.get("/v1/common/symbols", null, new TypeReference<ApiResponse<List<Symbol>>>() {
                });
        return resp.checkAndReturn();
    }


}
