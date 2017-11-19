package framework.injection.interface_inj.injectors;

import framework.injection.interface_inj.MovieFinder;

/**
 * Created by denysburlakov on 17.09.17.
 */
public class ColonMovieFinder implements Injector, MovieFinder, InjectFinderFilename {
    private String filename;
    public void injectFilename(String filename) {
            this.filename = filename;
    }
    public void injectInto(Object target) {
        ((InjectFinder) target).injectFinder(this);
    }
}

//1. find all beans, that has Inject interface
//2. call injectInto method for these beans, pass