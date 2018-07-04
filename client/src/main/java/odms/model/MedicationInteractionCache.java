package odms.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import odms.model._abstract.TimedEntryCache;
import odms.model._enum.Directory;
import odms.model.datamodel.TimedCacheValue;
import odms.utils.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MedicationInteractionCache implements TimedEntryCache<String, TimedCacheValue<String>> {

    private ConcurrentMap<String, TimedCacheValue<String>> cache;

    public MedicationInteractionCache() {
        cache = new ConcurrentHashMap<>();
    }


    @Override
    public void add(String key, TimedCacheValue<String> value) {
        cache.put(key, value);
    }

    public void add(String drugA, String drugB, String value) {
        String key;
        //sort drugs alphabetically to avoid B-A and A-B both being in the cache
        if (drugA.compareToIgnoreCase(drugB) < 0) {
            key = drugA + "-" + drugB;
        } else {
            key = drugB + "-" + drugA;
        }

        cache.put(key, new TimedCacheValue<>(value));
    }

    @Override
    public TimedCacheValue<String> get(String key) {
        if (cache != null) {
            return cache.get(key);
        }
        return null;
    }

    public TimedCacheValue<String> get(String drugA, String drugB) {
        String key;
        //sort drugs alphabetically to avoid B-A and A-B both being in the cache
        if (drugA.compareToIgnoreCase(drugB) < 0) {
            key = drugA + "-" + drugB;
        } else {
            key = drugB + "-" + drugA;
        }

        return cache.get(key);
    }

    @Override
    public boolean evict(String key) {
        //cache.remove returns the old value associated with key
        //returns true if old value != null
        return cache.remove(key) != null;
    }

    @Override
    public void load(String filename) throws IOException {
        try {
            File inFile = new File(Directory.CACHE.directory() + filename);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            try (Reader reader = new FileReader(inFile)) {
                ConcurrentMap<String, TimedCacheValue<String>> result = gson.fromJson(reader, new TypeToken<ConcurrentMap<String, TimedCacheValue<String>>>() {
                }.getType());
                if (result == null) {
                    Log.warning("cache file was empty. existing cache is unchanged.");
                    throw new NullPointerException("loaded cache file is empty (Existing cache unchanged");
                }
                cache = result;
            }
        } catch (FileNotFoundException e) {
            Log.severe("Cache file not found", e);
            throw e;
        } catch (RuntimeException e) {
            Log.severe("runtime error when reading cache from file", e);
            throw e;
        } catch (IOException e) {
            Log.severe("failed to set up file reader", e);
            throw e;
        }
    }

    @Override
    public void save(String filename) throws IOException {
        Files.createDirectories(Paths.get(Directory.CACHE.directory()));
        File outFile = new File(Directory.CACHE.directory() + filename);
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.createNewFile();
        Gson gson = new GsonBuilder().create();
        try (FileWriter writer = new FileWriter(outFile)) {
            String cacheData = gson.toJson(cache);
            writer.write(cacheData);
        } catch (IOException ex) {
            Log.severe("failed to save cache to file", ex);
            throw ex;
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void removeOlderThan(LocalDateTime time) {
        for (Map.Entry<String, TimedCacheValue<String>> entry : cache.entrySet()) {
            if (entry.getValue().getDateTime().isBefore(time)) {
                cache.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
