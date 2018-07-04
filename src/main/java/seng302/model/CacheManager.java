package seng302.model;

import seng302.utils.Log;

import java.io.IOException;

/**
 * singleton class that contains one of each type of Cache in use. This is to ensure that only one of each type exists at a time
 */
public class CacheManager {
    private static final String DEFAULT_INTERACTIONS_CACHE = "medInteractions.json";
    private static CacheManager manager;
    private MedicationInteractionCache interactionCache;

    /**
     * instantiate and load all the caches on first start-up
     */
    private CacheManager() {
        interactionCache = new MedicationInteractionCache();
        try {
            interactionCache.load(DEFAULT_INTERACTIONS_CACHE);
        } catch (IOException ex) {
            Log.warning("could not load medication interactions cache. continuing with empty cache", ex);
        }

    }

    public static CacheManager getInstance() {
        if (manager == null) {
            manager = new CacheManager();
        }
        return manager;
    }

    public MedicationInteractionCache getInteractionCache() {
        return interactionCache;
    }

    /**
     * group method to save contents of all active caches
     */
    public void saveAll() {
        try {
            interactionCache.save(DEFAULT_INTERACTIONS_CACHE);
        } catch (IOException ex) {
            Log.warning("could not save medication interactions cache", ex);
        }
    }

}
