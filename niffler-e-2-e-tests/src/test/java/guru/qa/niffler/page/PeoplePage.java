package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeoplePage {

    private static final String ACCEPT_BUTTON_TEXT = "Accept";
    private static final String DECLINE_BUTTON_TEXT = "Decline";
    private static final String UNFRIEND_BUTTON_TEXT = "Unfriend";
    private static final String WAITING_LABEL_TEXT = "Waiting...";

    private final SelenideElement searchInput = $("input[placeholder='Search']");
    private final SelenideElement searchButton = $("button[aria-label='search']");
    private final SelenideElement friendsTab = $("a[href='/people/friends']");
    private final SelenideElement allPeopleTab = $("a[href='/people/all']");
    private final SelenideElement myFriendsHeader = $$(".MuiTableContainer-root h2.MuiTypography-root").findBy(exactText("My friends"));
    private final SelenideElement friendRequestsHeader = $$(".MuiTableContainer-root h2.MuiTypography-root").findBy(exactText("Friend requests"));
    private final SelenideElement previousPageButton = $("#page-prev");
    private final SelenideElement nextPageButton = $("#page-next");
    private final ElementsCollection friendsList = $$("#friends .MuiTableRow-root");
    private final ElementsCollection friendsNamesList = $$("#friends .MuiTableRow-root p.MuiTypography-body1");
    private final ElementsCollection friendRequestNamesList = $$("#requests .MuiTableRow-root p.MuiTypography-body1");
    private final SelenideElement friendsListTable = $("#friends");
    private final SelenideElement friendRequestsListTable = $("#requests");
    private final SelenideElement noUsersText = $$("p.MuiTypography-root").findBy(exactText("There are no users yet"));
    private final ElementsCollection allPeopleNamesList = $$("#all .MuiTableRow-root p.MuiTypography-body1");
    private final SelenideElement waitingLabel = $("span.MuiChip-label:contains('Waiting...')");
    private final SelenideElement loadingSpinner = $("div.MuiCircularProgress-root");

    public PeoplePage checkThatFriendPresentInFriendsList(String name) {
        loadingSpinner.shouldNotBe(visible);

        friendsNamesList.findBy(exactText(name))
                .shouldBe(visible)
                .closest("tr")
                .find(By.tagName("button"))
                .shouldHave(exactText(UNFRIEND_BUTTON_TEXT))
                .shouldBe(visible).shouldBe(enabled);

        return this;
    }

    public PeoplePage checkThatFriendNotPresentInFriendsList(String name) {
        friendsList.find(Condition.text(name))
                .shouldNotBe(Condition.visible);
        return this;
    }

    public PeoplePage checkThatFriendsListIsEmpty() {
        friendsListTable.shouldNot(exist);
        noUsersText.shouldBe(visible);
        return this;
    }

    public PeoplePage checkThatIncomingRequestPresentInFriendsList(String user) {

        friendRequestNamesList.findBy(exactText(user))
                .shouldBe(visible)
                .closest("tr")
                .findAll(By.tagName("button"))
                .find(exactText(ACCEPT_BUTTON_TEXT))
                .shouldBe(visible).shouldBe(enabled)
                .sibling(0)
                .shouldHave(exactText(DECLINE_BUTTON_TEXT))
                .shouldBe(visible).shouldBe(enabled);
        return this;
    }

    public PeoplePage clickAllPeopleTab() {
        allPeopleTab.click();
        loadingSpinner.shouldNotBe(visible);
        return this;
    }

    public void checkThatOutgoingRequestPresentInAllPeoplesList(String user) {

        allPeopleNamesList.findBy(exactText(user))
                .shouldBe(visible)
                .closest("tr")
                .find(By.cssSelector("span.MuiChip-label"))
                .shouldHave(exactText(WAITING_LABEL_TEXT))
                .shouldBe(visible);
    }
}