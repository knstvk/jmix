package ${project_rootPackage}.screen.user;

import ${project_rootPackage}.entity.User;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;

@UiController("${project_idPrefix}_User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@LoadDataBeforeShow
@Route("users")
public class UserBrowse extends StandardLookup<User> {
}