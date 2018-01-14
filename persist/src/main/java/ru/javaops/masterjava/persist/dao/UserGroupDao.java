package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.UserGroup;

import java.util.List;
import java.util.Set;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserGroupDao implements AbstractDao {
    @SqlUpdate("TRUNCATE user_group CASCADE")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT user_id FROM user_group WHERE group_id=:it")
    public abstract Set<Integer> getUserIds(@Bind int groupId);

    public static List<UserGroup> toUserGroups(int userId, Integer... groupIds) {
        return StreamEx.of(groupIds).map(groupId -> new UserGroup(userId, groupId)).toList();
    }

    public static Set<Integer> getByGroupId(int groupId, List<UserGroup> userGroups) {
        return StreamEx.of(userGroups).filter(ug -> ug.getGroupId() == groupId).map(UserGroup::getGroupId).toSet();
    }
}
