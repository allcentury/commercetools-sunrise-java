package wedecidelatercommon;

import play.mvc.Call;

public interface HomeReverseRouter {

    Call homePageCall(final String languageTag);

}
