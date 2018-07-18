package odms.commons.model;

import org.junit.Assert;
import org.junit.Test;

public class UserBuilderTest {
    private UserBuilder builder;


    @Test
    public void detailsNotCarriedBetweenUsers() {
        User first = (new UserBuilder()).setFirstName("steve").build();
        User second = (new UserBuilder()).build();
        Assert.assertNotEquals(first.getFirstName(), second.getFirstName());
    }

    @Test
    public void builtChangesNotUndoable() {
        User testUser = (new UserBuilder()).setMiddleName("heyoo").build();
        String nameBefore = testUser.getMiddleName();
        testUser.undo();
        String nameAfter = testUser.getMiddleName();

        Assert.assertEquals(nameBefore, nameAfter);
    }

    @Test
    public void builtUserCanBeChangedAfter() {
        //check that the new user can be modified, even after the builder is disposed of
        //explicitly declare the builder so it can be explicitly nulled
        UserBuilder builder = new UserBuilder();
        User testUser = builder.setFirstName("Mittens").build();
        builder = null;

        testUser.setFirstName("not Mittens");
    }

    @Test
    public void builtUserIsUndoableAfter() {
        final String originalName = "Mittens";
        final String newName = "not Mittens";
        User testUser = (new UserBuilder()).setFirstName(originalName).build();
        testUser.setFirstName(newName);
        testUser.undo();

        Assert.assertEquals(originalName, testUser.getFirstName());
    }
}
