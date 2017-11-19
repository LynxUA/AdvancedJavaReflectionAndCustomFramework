package framework.injection.interface_inj.injectors;

/**
 * Created by denysburlakov on 18.09.17.
 */
public class FinderFilenameInjector implements Injector {
    public void injectInto(Object target) {
        ((InjectFinderFilename)target).injectFilename("movies1.txt");
    }
}
