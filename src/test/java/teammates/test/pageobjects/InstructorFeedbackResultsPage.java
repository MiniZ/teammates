package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.AssertHelper;

public class InstructorFeedbackResultsPage extends AppPage {

    @FindBy(id = "button_sortFromName")
    public WebElement sortTableGiverButton;

    @FindBy(id = "collapse-panels-button")
    public WebElement collapseExpandButton;

    @FindBy(id = "collapse-panels-button-team-0")
    public WebElement instructorPanelCollapseStudentsButton;

    @FindBy(id = "collapse-panels-button-section-0")
    public WebElement sectionCollapseStudentsButton;

    @FindBy(id = "show-stats-checkbox")
    public WebElement showStatsCheckbox;

    @FindBy(id = "indicate-missing-responses-checkbox")
    public WebElement indicateMissingResponsesCheckbox;

    public InstructorFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Session Results</h1>");
    }

    public String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }

    public String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }

    @Override
    public void waitForPageToLoad() {
        super.waitForPageToLoad();
        // results page has panels that are loaded by ajax,
        // and these panels expand when their contents are loaded
        waitForPanelsToExpand();
    }

    /**
     * Waits until the page structure is loaded.
     * Does not wait for all the content that are loaded by ajax to load.
     */
    public void waitForPageStructureToLoad() {
        super.waitForPageToLoad();
    }

    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    public void displayByGiverRecipientQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Recipient > Question");
        expandPanels();
    }

    public void displayByRecipientGiverQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Giver > Question");
        expandPanels();
    }

    public void displayByGiverQuestionRecipient() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Question > Recipient");
        expandPanels();
    }

    public void displayByRecipientQuestionGiver() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Question > Giver");
        expandPanels();
    }

    public void filterResponsesForSection(String section) {
        Select select = new Select(browser.driver.findElements(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION))
                                                 .get(1));
        select.selectByVisibleText(section);
        expandPanels();
    }

    public void filterResponsesForAllSections() {
        Select select = new Select(browser.driver.findElements(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION))
                                                 .get(1));
        select.selectByVisibleText("All");
        expandPanels();
    }

    public void displayByQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Question");
        expandPanels();
    }

    public void clickGroupByTeam() {
        WebElement button = browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM));
        click(button);
        expandPanels();
    }

    public void clickCollapseExpand() {
        click(collapseExpandButton);
    }

    public void clickShowStats() {
        click(showStatsCheckbox);
    }

    public void clickIndicateMissingResponses() {
        click(indicateMissingResponsesCheckbox);
        expandPanels();
    }

    public void expandPanels() {
        try {
            // in case the panels are already expanded
            if (!collapseExpandButton.getText().contains("Expand")) {
                return;
            }

            clickCollapseExpand();
            waitForPageToLoad();
        } catch (NoSuchElementException e) {
            // Pages with no feedback response do not have button element
            // We need to click on each of the panels to expand them
            List<WebElement> panels = browser.driver.findElements(By.className("panel-heading"));
            for (WebElement panel : panels) {
                click(panel);
            }
            waitForPanelsToExpand();
        }
    }

    public void fillSearchBox(String s) {
        this.fillTextBox(browser.driver.findElement(By.id("results-search-box")), s);
    }

    public InstructorFeedbackEditPage clickEditLink() {
        WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
        click(button);

        InstructorFeedbackEditPage editPage = changePageType(InstructorFeedbackEditPage.class);
        editPage.waitForPageToLoad();
        return editPage;
    }

    public void clickQuestionAdditionalInfoButton(int qnNumber, String additionalInfoId) {
        click(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));
    }

    public boolean isQuestionAdditionalInfoVisible(int qnNumber, String additionalInfoId) {
        return isElementVisible("questionAdditionalInfo-" + qnNumber + "-" + additionalInfoId);
    }

    public String getQuestionAdditionalInfoButtonText(int qnNumber, String additionalInfoId) {
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-"
                                                                             + qnNumber + "-" + additionalInfoId));
        return qnAdditionalInfoButton.getText();
    }

    public void addFeedbackResponseComment(String addResponseCommentId, String commentText) {
        WebElement addResponseCommentForm = browser.driver.findElement(By.id(addResponseCommentId));
        WebElement parentContainer = addResponseCommentForm.findElement(By.xpath("../.."));
        WebElement showResponseCommentAddFormButton = parentContainer.findElement(By.id("button_add_comment"));
        click(showResponseCommentAddFormButton);
        WebElement editorElement = addResponseCommentForm.findElement(By.className("mce-content-body"));
        waitForRichTextEditorToLoad(editorElement.getAttribute("id"));
        fillRichTextEditor(editorElement.getAttribute("id"), commentText);
        click(addResponseCommentForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        if (commentText.isEmpty()) {
            // empty comment: wait until the textarea is clickable again
            waitForElementToBeClickable(editorElement);
        } else {
            // non-empty comment: wait until the add comment form disappears
            waitForElementToDisappear(By.id(addResponseCommentId));
        }
    }

    public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        click(commentRow.findElements(By.tagName("a")).get(1));

        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        fillRichTextEditor("responsecommenttext" + commentIdSuffix, newCommentText);
        click(commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        ThreadHelper.waitFor(1000);
    }

    public void clickVisibilityOptionForResponseCommentAndSave(String idString, int numOfTheCheckbox) {
        String idSuffix = idString.substring(18);
        WebElement commentRow = browser.driver.findElement(By.id(idString));
        click(commentRow.findElements(By.tagName("a")).get(1));
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + idSuffix));
        click(commentRow.findElement(By.id("frComment-visibility-options-trigger" + idSuffix)));
        click(commentRow.findElements(By.cssSelector("input[type='checkbox']")).get(numOfTheCheckbox));
        click(commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        ThreadHelper.waitFor(1000);
    }

    /**
     * Makes sure the result panels are indeed all visible.
     */
    public void verifyResultsVisible() {
        assertTrue(isAllResultsPanelBodyVisibilityEquals(true));
    }

    /**
     * Makes sure the result panels are indeed all hidden.
     */
    public void verifyResultsHidden() {
        assertTrue(isAllResultsPanelBodyVisibilityEquals(false));
    }

    /**
     * Checks if the body of all the results panels are collapsed or expanded.
     * @param isVisible true to check for expanded, false to check for collapsed.
     * @return true if all results panel body are equals to the visibility being checked.
     */
    private boolean isAllResultsPanelBodyVisibilityEquals(boolean isVisible) {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);
        int numOfQns = webElements.size();

        assertTrue(numOfQns > 0);

        for (WebElement e : webElements) {
            if (e.isDisplayed() != isVisible) {
                return false;
            }
        }

        return true;
    }

    /**
     * Waits for all the panels to collapse.
     */
    public void waitForPanelsToCollapse() {
        By panelCollapseSelector = By.cssSelector("div[id^='panelBodyCollapse-']");

        waitForElementsToDisappear(browser.driver.findElements(panelCollapseSelector));
    }

    /**
     * Waits for all the panels to expand.
     */
    public void waitForPanelsToExpand() {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);
        for (WebElement element : webElements) {
            try {
                waitForElementVisibility(element);
            } catch (StaleElementReferenceException e) {
                // Case when element has been removed after JS processing
            }
        }
    }

    public boolean verifyAllStatsVisibility() {
        for (WebElement e : browser.driver.findElements(By.className("resultStatistics"))) {
            if ("none".equals(e.getCssValue("display"))) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyMissingResponsesVisibility() {
        List<WebElement> pendingResponses = browser.driver.findElements(By.className("pending_response_row"));
        return pendingResponses.isEmpty();
    }

    public void deleteFeedbackResponseComment(String commentIdSuffix) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        click(commentRow.findElement(By.tagName("form")).findElement(By.tagName("a")));
        waitForConfirmationModalAndClickOk();
        ThreadHelper.waitFor(1500);
    }

    public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
        By commentRowSelector = By.id("responseCommentRow" + commentRowIdSuffix);
        WebElement commentRow = waitForElementPresence(commentRowSelector);
        waitForTextContainedInElementPresence(By.id("plainCommentText" + commentRowIdSuffix), commentText);
        assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains(giverName)
                   || commentRow.findElement(By.className("text-muted")).getText().contains("you"));
    }

    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        WebElement errorMessageSpan = waitForElementPresence(By.cssSelector("#errorMessage"));
        assertEquals(errorMessage, errorMessageSpan.getText());
    }

    public void verifyRowMissing(String rowIdSuffix) {
        try {
            waitForAjaxLoaderGifToDisappear();
            browser.driver.findElement(By.id("responseCommentRow" + rowIdSuffix));
            fail("Row expected to be missing found.");
        } catch (NoSuchElementException expected) {
            // row expected to be missing
            return;
        }
    }

    public void clickAjaxLoadResponsesPanel(int index) {
        List<WebElement> ajaxPanels = browser.driver.findElements(By.cssSelector(".ajax_submit"));
        click(ajaxPanels.get(index));
    }

    public void clickAjaxNoResponsePanel() {
        WebElement ajaxPanels = browser.driver.findElement(By.cssSelector(".ajax-response-submit"));
        click(ajaxPanels);
    }

    public void clickViewPhotoLink(String panelBodyIndex, String urlRegex) {
        String panelBodySelector = "#panelBodyCollapse-" + panelBodyIndex;
        String popoverSelector = panelBodySelector + " .popover-content";

        browser.driver.findElement(By.cssSelector(panelBodySelector + " .profile-pic-icon-click a")).click();

        String imgSrc = getElementSrcWithRetryAfterWaitForPresence(By.cssSelector(popoverSelector + " > img"));
        AssertHelper.assertContainsRegex(urlRegex, imgSrc);
    }

    public void hoverClickAndViewStudentPhotoOnHeading(String panelHeadingIndex, String urlRegex) {
        String headingSelector = "#panelHeading-" + panelHeadingIndex;
        String popoverSelector = headingSelector + " .popover-content";

        moveToElement(By.cssSelector(headingSelector + " .profile-pic-icon-hover"));
        waitForElementPresence(By.cssSelector(popoverSelector + " > a")).click();

        String imgSrc = getElementSrcWithRetryAfterWaitForPresence(By.cssSelector(popoverSelector + " > img"));
        AssertHelper.assertContainsRegex(urlRegex, imgSrc);
    }

    public void hoverAndViewStudentPhotoOnBody(String panelBodyIndex, String urlRegex) {
        String bodyRowSelector = "#panelBodyCollapse-" + panelBodyIndex + " > .panel-body > .row";
        String popoverSelector = bodyRowSelector + " .popover-content";

        moveToElement(By.cssSelector(bodyRowSelector + " .profile-pic-icon-hover"));

        String imgSrc = getElementSrcWithRetryAfterWaitForPresence(By.cssSelector(popoverSelector + " > img"));
        AssertHelper.assertContainsRegex(urlRegex, imgSrc);
    }

    public void hoverClickAndViewPhotoOnTableCell(int questionBodyIndex, int tableRow,
                                                  int tableCol, String urlRegex) {
        String cellSelector = "#questionBody-" + questionBodyIndex + " .dataTable tbody"
                              + " tr:nth-child(" + (tableRow + 1) + ")"
                              + " td:nth-child(" + (tableCol + 1) + ")";
        String popoverSelector = cellSelector + " .popover-content";

        moveToElement(By.cssSelector(cellSelector + " .profile-pic-icon-hover"));
        waitForElementPresence(By.cssSelector(popoverSelector + " > a")).click();

        String imgSrc = getElementSrcWithRetryAfterWaitForPresence(By.cssSelector(popoverSelector + " > img"));
        AssertHelper.assertContainsRegex(urlRegex, imgSrc);
    }

    public void hoverClickAndViewGiverPhotoOnTableCell(int questionBodyIndex, int tableRow,
                                                       String urlRegex) {
        hoverClickAndViewPhotoOnTableCell(questionBodyIndex, tableRow, 1, urlRegex);
    }

    public void hoverClickAndViewRecipientPhotoOnTableCell(int questionBodyIndex, int tableRow,
                                                           String urlRegex) {
        hoverClickAndViewPhotoOnTableCell(questionBodyIndex, tableRow, 3, urlRegex);
    }

    public void removeNavBar() {
        executeScript("document.getElementsByClassName('navbar-fixed-top')[0].parentNode.removeChild("
                      + "document.getElementsByClassName('navbar-fixed-top')[0])");
    }

    public void verifyModerateResponseButtonBelongsTo(WebElement btn, String email) {
        assertEquals(email, btn.findElement(By.xpath("input[5]")).getAttribute("value"));
    }

    public WebElement getModerateResponseButtonInQuestionView(int qnNo, int responseNo) {
        return browser.driver.findElement(By.id("questionBody-" + (qnNo - 1)))
                             .findElement(By.className("table-responsive"))
                             .findElement(By.xpath("table/tbody/tr[" + responseNo + "]/td[6]/form"));
    }

    public void clickInstructorPanelCollapseStudentsButton() {
        click(instructorPanelCollapseStudentsButton);
    }

    public void clickSectionCollapseStudentsButton() {
        click(sectionCollapseStudentsButton);
    }

    public void waitForInstructorPanelStudentPanelsToCollapse() {
        List<WebElement> studentPanels = browser.driver.findElements(
                By.cssSelector("#panelBodyCollapse-0-1 .panel-collapse"));
        waitForElementsToDisappear(studentPanels);
    }

    public void waitForSectionStudentPanelsToCollapse() {
        List<WebElement> studentPanels = browser.driver.findElements(
                By.cssSelector("#panelBodyCollapse-section-0-1 .panel-collapse"));
        waitForElementsToDisappear(studentPanels);
    }

    public void verifyPanelForParticipantIsDisplayed(String participantIdentifier) {
        WebElement panel = browser.driver.findElement(
                By.xpath("//div[contains(@class, 'panel-primary') or contains(@class, 'panel-default')]"
                            + "[contains(.,'[" + participantIdentifier + "]')]"));
        assertTrue(panel.isDisplayed());
    }

    public void verifySpecifiedPanelIdsAreCollapsed(String[] ids) {
        for (String id : ids) {
            WebElement panel = browser.driver.findElement(By.id("panelBodyCollapse-" + id));
            assertFalse(panel.isDisplayed());
        }
    }

    public boolean isSectionPanelExist(String section) {
        List<WebElement> panels = browser.driver.findElements(By.cssSelector("div[id^='panelHeading-']"));
        for (WebElement panel : panels) {
            String panelSectionName = panel.findElement(By.className("panel-heading-text")).getText();
            if (panelSectionName.equals(section)) {
                return true;
            }
        }
        return false;
    }

    public void changeFsNameInAjaxLoadResponsesForm(int indexOfForm, String newFsName) {
        executeScript("$('.ajax_submit:eq(" + indexOfForm + ") [name=\"fsname\"]').val('" + newFsName + "')");
    }

    public void changeFsNameInNoResponsePanelForm(String newFsName) {
        executeScript("$('.ajax-response-submit [name=\"fsname\"]').val('" + newFsName + "')");
    }

    public void waitForAjaxError(int indexOfForm) {
        By ajaxErrorSelector = By.cssSelector(".ajax_submit:nth-of-type(" + indexOfForm
                                        + ") .ajax-error");
        waitForElementPresence(ajaxErrorSelector);

        waitForTextContainedInElementPresence(ajaxErrorSelector, "[ Failed to load. Click here to retry. ]");
    }

    public void waitForAjaxErrorOnNoResponsePanel() {
        By ajaxErrorSelector = By.cssSelector(".ajax-response-submit .ajax-error");
        waitForElementPresence(ajaxErrorSelector);

        waitForTextContainedInElementPresence(ajaxErrorSelector, "[ Failed to load. Click here to retry. ]");
    }

    private void moveToElement(By by) {
        WebElement element = browser.driver.findElement(by);
        new Actions(browser.driver).moveToElement(element).perform();
    }

    private String getElementSrcWithRetryAfterWaitForPresence(By by) {
        try {
            return waitForElementPresence(by).getAttribute("src");
        } catch (StaleElementReferenceException e) {
            // Element changed (e.g. loading gif changed to actual image)
            return waitForElementPresence(by).getAttribute("src");
        }
    }

}
