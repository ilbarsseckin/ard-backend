package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.entity.SystemSetting;
import com.ilbarslab.ardbackend.print.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository repo;

    public String get(String key, String defaultValue) {
        return repo.findByKey(key).map(SystemSetting::getValue).orElse(defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        try { return Double.parseDouble(get(key, String.valueOf(defaultValue))); }
        catch (Exception e) { return defaultValue; }
    }

    public void set(String key, String value, String description) {
        SystemSetting s = repo.findByKey(key).orElse(SystemSetting.builder().key(key).description(description).build());
        s.setValue(value);
        repo.save(s);
    }

    public Map<String, String> getAll() {
        Map<String, String> map = new HashMap<>();
        repo.findAll().forEach(s -> map.put(s.getKey(), s.getValue()));
        return map;
    }
}
