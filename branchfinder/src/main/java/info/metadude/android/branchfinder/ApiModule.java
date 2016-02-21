package info.metadude.android.branchfinder;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public final class ApiModule {

    public static BranchFinderService provideBranchFinderService(final String baseUrl, ErrorHandler errorHandler) {
        return provideBranchFinderService(baseUrl, errorHandler, null);
    }

    public static BranchFinderService provideBranchFinderService(
            final String baseUrl, ErrorHandler errorHandler, final OkHttpClient okHttpClient) {
        return createRetrofit(baseUrl, errorHandler, okHttpClient)
                .create(BranchFinderService.class);
    }

    private static RestAdapter createRetrofit(String baseUrl, ErrorHandler errorHandler, OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setErrorHandler(errorHandler)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(BuildConfig.DEBUG ?
                        RestAdapter.LogLevel.HEADERS :
                        RestAdapter.LogLevel.NONE)
                .build();
    }

}
