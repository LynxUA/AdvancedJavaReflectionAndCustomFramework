package framework.injection.interface_inj;

import framework.injection.interface_inj.injectors.InjectFinder;

/**
 * Created by denysburlakov on 17.09.17.
 */
public class MovieLister implements InjectFinder {
    private MovieFinder finder;
    public void injectFinder(MovieFinder finder) {
        this.finder = finder;
    }

    public MovieFinder getFinder() {
        return finder;
    }
}