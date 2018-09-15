package odms.commons.database;

import odms.commons.model.datamodel.OrgansWithDisqualification;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

public class DisqualifiedOrgansHandler {

    public Collection<OrgansWithDisqualification> getDisqualifiedOrgans(Connection connection) {
        //Code for constructing sql goes here
        return new ArrayList<>();
    }

    public void postDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) {
        //for organ in disqualifications
            //Code for constructing sql goes here
    }

    public void deleteDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) {
        //for organ in disqualifications
            //Code for constructing sql goes here
    }
}
