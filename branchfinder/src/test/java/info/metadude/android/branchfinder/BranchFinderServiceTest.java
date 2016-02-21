package info.metadude.android.branchfinder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import info.metadude.android.branchfinder.models.BoundingBox;
import info.metadude.android.branchfinder.models.Branch;
import info.metadude.android.branchfinder.models.GeoPoint;
import info.metadude.android.branchfinder.models.OpeningHour;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class BranchFinderServiceTest {

    protected BranchFinderService branchFinderService;

    @Before
    public void initService() {
        branchFinderService = ApiModule.provideBranchFinderService("https://www.barzahlen.de", new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                return null;
            }
        });
    }

    @Test
    public void testThatProductionServerIsReachable() {
        GeoPoint southWest = new GeoPoint(52.48675300749431, 13.35877465576175);
        GeoPoint northEast = new GeoPoint(52.54420821064123, 13.444605344238312);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        String mapBounds = MapBoundsFormatter.getFormattedString(boundingBox);
        List<Branch> branches = branchFinderService.getBranches(mapBounds);
        assertThat(branches)
                .isNotNull()
                .isNotEmpty();
        for (Branch branch : branches) {
            testBranch(branch);
        }
    }

    private void testBranch(Branch branch) {
        assertThat(branch.getCity()).isNotNull().isNotEmpty();
        assertThat(branch.getId()).isNotNull().isGreaterThan(0);
        assertThat(branch.getLat()).isNotNull().isNotEmpty();
        assertThat(branch.getLng()).isNotNull().isNotEmpty();
        assertThat(branch.getLogoUrl()).isNotNull().isNotEmpty();
        List<OpeningHour> openingHours = branch.getOpeningHours();
        for (OpeningHour openingHour : openingHours) {
            testOpeningHour(openingHour);
        }
        assertThat(branch.getStreetNo()).isNotNull().isNotEmpty();
        assertThat(branch.getStreetNr()).isNotNull().isNotEmpty();
        assertThat(branch.getTitle()).isNotNull().isNotEmpty();
    }

    private void testOpeningHour(OpeningHour openingHour) {
        assertThat(openingHour.getDays()).isNotNull().isNotEmpty();
        assertThat(openingHour.getTime()).isNotNull().isNotEmpty();
    }

}
