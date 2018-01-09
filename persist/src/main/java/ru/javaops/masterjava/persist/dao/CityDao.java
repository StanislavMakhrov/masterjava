package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;
import java.util.Map;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {
    @SqlUpdate("TRUNCATE city CASCADE")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM city ORDER by name")
    public abstract List<City> getAll();

    public Map<String, City> getAsMap() {
        return StreamEx.of(getAll()).toMap(City::getName, g -> g);
    }

    @SqlUpdate("INSERT INTO city (ref, name)  VALUES (:ref, :name)")
    @GetGeneratedKeys
    public abstract int insert(@BindBean City city);

    @SqlBatch("INSERT INTO city (ref, name)  VALUES (:ref, :name)")
    public abstract void insertBatch(@BindBean List<City> cities);
}
