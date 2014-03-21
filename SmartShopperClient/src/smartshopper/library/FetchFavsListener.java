package smartshopper.library;

import java.util.List;

public interface FetchFavsListener {
    public void onFetchComplete(List<Product> data);
    public void onFetchFailure(String msg);
}

