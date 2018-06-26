package seng302.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import seng302.model._abstract.TimedEntryCache;
import seng302.model._enum.Directory;
import seng302.model.datamodel.TimedCacheValue;
import seng302.utils.Log;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    @Override
    public TimedCacheValue<String> get(String key) {
        if (cache != null) {
            return cache.get(key);
        }
        return null;
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
                cache = gson.fromJson(reader, new TypeToken<ConcurrentMap<String, TimedCacheValue<String>>>() {
                }.getType());
            }
        } catch (FileNotFoundException e) {
            Log.severe("Cache file not found", e);
            //throw e;
        } catch (RuntimeException e) {
            Log.severe("runtime error when reading cache from file", e);
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
        throw new NotImplementedException();
    }
}
