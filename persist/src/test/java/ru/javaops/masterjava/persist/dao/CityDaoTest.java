package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.model.City;

import java.util.Map;

import static ru.javaops.masterjava.persist.dao.CityTestData.CITIES;

public class CityDaoTest extends AbstractDaoTest<CityDao> {

    public CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        CityDaoTest.init();
    }

    @Before
    public void setUp() throws Exception {
        CityTestData.setUp();
    }

    @Test
    public void getAll() throws Exception {
        final Map<String, City> cities = dao.getAsMap();
        Assert.assertEquals(CITIES, cities);
        System.out.println(cities.values());
    }

    @Test
    public void insert() throws Exception {
        City ekb = new City("ekb", "Екатеринбург");
        dao.insert(ekb);
        final Map<String, City> cities = dao.getAsMap();
        Assert.assertEquals(ImmutableMap.of(CITIES.values(), ekb), cities);
        System.out.println(cities.values());
    }
}
