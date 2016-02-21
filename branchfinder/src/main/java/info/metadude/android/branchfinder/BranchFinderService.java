package info.metadude.android.branchfinder;

import java.util.List;

import info.metadude.android.branchfinder.models.Branch;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface BranchFinderService {

    @Headers({
            "Accept: application/json"
    })
    @GET("/filialfinder/get_stores")
    List<Branch> getBranches(
            @Query("map_bounds") String mapBounds
    );

}
