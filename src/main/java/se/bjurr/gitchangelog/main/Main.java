package se.bjurr.gitchangelog.main;

import static com.google.common.base.Preconditions.checkArgument;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.internal.settings.Settings.defaultSettings;
import static se.softhouse.jargo.Arguments.helpArgument;
import static se.softhouse.jargo.Arguments.optionArgument;
import static se.softhouse.jargo.Arguments.stringArgument;
import static se.softhouse.jargo.CommandLineParser.withArguments;

import java.io.File;

import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

import com.google.common.annotations.VisibleForTesting;

public class Main {
 public static final String PARAM_SETTINGS_FILE = "-sf";
 public static final String PARAM_OUTPUT_FILE = "-of";
 public static final String PARAM_OUTPUT_STDOUT = "-std";
 public static final String PARAM_TEMPLATE = "-t";
 public static final String PARAM_REPO = "-r";
 public static final String PARAM_FROM_REF = "-fr";
 public static final String PARAM_TO_REF = "-tr";
 public static final String PARAM_FROM_COMMIT = "-fc";
 public static final String PARAM_TO_COMMIT = "-tc";
 public static final String PARAM_IGNORE_PATTERN = "-ip";
 public static final String PARAM_JIRA_SERVER = "-js";
 public static final String PARAM_JIRA_ISSUE_PATTERN = "-jp";
 public static final String PARAM_GITHUB_SERVER = "-gs";
 public static final String PARAM_GITHUB_PATTERN = "-gp";
 public static final String PARAM_CUSTOM_ISSUE_NAME = "-cn";
 public static final String PARAM_CUSTOM_ISSUE_PATTERN = "-cp";
 public static final String PARAM_CUSTOM_ISSUE_LINK = "-cl";
 public static final String PARAM_UNTAGGED_TAG_NAME = "-ut";
 public static final String PARAM_TIMEZONE = "-tz";
 public static final String PARAM_DATEFORMAT = "-df";
 public static final String PARAM_NOISSUE = "-ni";
 public static final String PARAM_READABLETAGNAME = "-rt";
 private static String systemOutPrintln;
 private static boolean recordSystemOutPrintln;

 public static void main(String args[]) throws Exception {
  Settings defaultSettings = defaultSettings();
  Argument<?> helpArgument = helpArgument("-h", "--help");

  Argument<String> settingsArgument = stringArgument(PARAM_SETTINGS_FILE, "--settings-file")//
    .description("Use settings from file.")//
    .defaultValue(null) //
    .build();
  Argument<Boolean> outputStdoutArgument = optionArgument(PARAM_OUTPUT_STDOUT, "--stdout")//
    .description("Print builder to <STDOUT>.")//
    .build();
  Argument<String> outputFileArgument = stringArgument(PARAM_OUTPUT_FILE, "--output-file")//
    .description("Write output to file.")//
    .build();

  Argument<String> templatePathArgument = stringArgument(PARAM_TEMPLATE, "--template")//
    .description("Template to use. A default template will be used if not specified.")//
    .defaultValue(defaultSettings.getTemplatePath())//
    .build();

  Argument<String> untaggedTagNameArgument = stringArgument(PARAM_UNTAGGED_TAG_NAME, "--untaggedName")//
    .description(
      "When listing commits per tag, this will by the name of a virtual tag that contains commits not available in any git tag.")//
    .defaultValue(defaultSettings.getUntaggedName())//
    .build();

  Argument<String> fromRepoArgument = stringArgument(PARAM_REPO, "--repo")//
    .description("Repository.")//
    .defaultValue(defaultSettings.getFromRepo())//
    .build();
  Argument<String> fromRefArgument = stringArgument(PARAM_FROM_REF, "--fromRef")//
    .description("From ref.")//
    .defaultValue(defaultSettings.getFromRef())//
    .build();
  Argument<String> toRefArgument = stringArgument(PARAM_TO_REF, "--toRef")//
    .description("To ref.")//
    .defaultValue(defaultSettings.getToRef())//
    .build();
  Argument<String> fromCommitArgument = stringArgument(PARAM_FROM_COMMIT, "--fromCommit")//
    .description("From commit.")//
    .defaultValue(defaultSettings.getFromCommit())//
    .build();
  Argument<String> toCommitArgument = stringArgument(PARAM_TO_COMMIT, "--toCommit")//
    .description("To commit.")//
    .defaultValue(defaultSettings.getToCommit())//
    .build();

  Argument<String> ignoreCommitsIfMessageMatchesArgument = stringArgument(PARAM_IGNORE_PATTERN, "--ignorePattern")//
    .description("Ignore commits where pattern matches message.")//
    .defaultValue(defaultSettings.getIgnoreCommitsIfMessageMatches())//
    .build();

  Argument<String> jiraServerArgument = stringArgument(PARAM_JIRA_SERVER, "--jiraServer")//
    .description("Jira server. When a Jira server is given, the title of the Jira issues can be used in the changelog.")//
    .defaultValue(defaultSettings.getJiraServer().orNull())//
    .build();
  Argument<String> jiraIssuePatternArgument = stringArgument(PARAM_JIRA_ISSUE_PATTERN, "--jiraPattern")//
    .description("Jira issue pattern.")//
    .defaultValue(defaultSettings.getJiraIssuePattern().orNull())//
    .build();

  Argument<String> githubServerArgument = stringArgument(PARAM_GITHUB_SERVER, "--githubServer")//
    .description(
      "Github server. When a Github server is given, the title of the Github issues can be used in the changelog.")//
    .defaultValue(defaultSettings.getGithubServer().orNull())//
    .build();
  Argument<String> githubIssuePatternArgument = stringArgument(PARAM_GITHUB_PATTERN, "--githubPattern")//
    .description("Github pattern.")//
    .defaultValue(defaultSettings.getGithubIssuePattern().orNull())//
    .build();

  Argument<String> customIssueNameArgument = stringArgument(PARAM_CUSTOM_ISSUE_NAME, "--customIssueName")//
    .description("Custom issue name.")//
    .defaultValue(null)//
    .build();
  Argument<String> customIssuePatternArgument = stringArgument(PARAM_CUSTOM_ISSUE_PATTERN, "--customIssuePattern")//
    .description("Custom issue pattern.")//
    .defaultValue(null)//
    .build();
  Argument<String> customIssueLinkArgument = stringArgument(PARAM_CUSTOM_ISSUE_LINK, "--customIssueLink")//
    .description("Custom issue link.")//
    .defaultValue(null)//
    .build();

  Argument<String> timeZoneArgument = stringArgument(PARAM_TIMEZONE, "--timeZone")//
    .description("TimeZone to use when printing dates.")//
    .defaultValue(defaultSettings.getTimeZone())//
    .build();
  Argument<String> dateFormatArgument = stringArgument(PARAM_DATEFORMAT, "--date-format")//
    .description("Format to use when printing dates.")//
    .defaultValue(defaultSettings.getDateFormat())//
    .build();
  Argument<String> noIssueArgument = stringArgument(PARAM_NOISSUE, "--no-issue-name")//
    .description("Name of virtual issue that contains commits that has no issue associated.")//
    .defaultValue(defaultSettings.getNoIssueName())//
    .build();
  Argument<String> readableTagNameArgument = stringArgument(PARAM_READABLETAGNAME, "--readable-tag-name")//
    .description("Pattern to extract readable part of tag.")//
    .defaultValue(defaultSettings.getReadableTagName())//
    .build();

  try {
   ParsedArguments arg = withArguments(helpArgument, settingsArgument, outputStdoutArgument, outputFileArgument,
     templatePathArgument, fromCommitArgument, fromRefArgument, fromRepoArgument, toCommitArgument, toRefArgument,
     untaggedTagNameArgument, jiraIssuePatternArgument, jiraServerArgument, ignoreCommitsIfMessageMatchesArgument,
     githubIssuePatternArgument, githubServerArgument, customIssueLinkArgument, customIssueNameArgument,
     customIssuePatternArgument, timeZoneArgument, dateFormatArgument, noIssueArgument, readableTagNameArgument)//
     .parse(args);

   GitChangelogApi changelogApiBuilder = gitChangelogApiBuilder();

   if (arg.wasGiven(settingsArgument)) {
    changelogApiBuilder.withSettings(new File(arg.get(settingsArgument)).toURI().toURL());
   }

   changelogApiBuilder //
     .withFromRepo(arg.get(fromRepoArgument)) //
     .withUntaggedName(arg.get(untaggedTagNameArgument)) //
     .withIgnoreCommitsWithMesssage(arg.get(ignoreCommitsIfMessageMatchesArgument)) //
     .withTemplatePath(arg.get(templatePathArgument)) //
     .withJiraIssuePattern(arg.get(jiraIssuePatternArgument)) //
     .withJiraServer(arg.get(jiraServerArgument)) //
     .withGithubIssuePattern(arg.get(githubIssuePatternArgument)) //
     .withGithubServer(arg.get(githubServerArgument)) //
     .withTimeZone(arg.get(timeZoneArgument))//
     .withDateFormat(arg.get(dateFormatArgument))//
     .withNoIssueName(arg.get(noIssueArgument))//
     .withReadableTagName(arg.get(readableTagNameArgument));

   if (arg.wasGiven(fromCommitArgument)) {
    changelogApiBuilder.withFromCommit(arg.get(fromCommitArgument));
    changelogApiBuilder.withFromRef(null);
   }
   if (arg.wasGiven(fromRefArgument)) {
    changelogApiBuilder.withFromCommit(null);
    changelogApiBuilder.withFromRef(arg.get(fromRefArgument));
   }
   if (arg.wasGiven(toCommitArgument)) {
    changelogApiBuilder.withToCommit(arg.get(toCommitArgument));
    changelogApiBuilder.withToRef(null);
   }
   if (arg.wasGiven(toRefArgument)) {
    changelogApiBuilder.withToCommit(null);
    changelogApiBuilder.withToRef(arg.get(toRefArgument));
   }

   if ( //
   arg.wasGiven(customIssueNameArgument) && //
     arg.wasGiven(customIssuePatternArgument) && //
     arg.wasGiven(customIssueLinkArgument)) {
    changelogApiBuilder.withCustomIssues(//
      arg.get(customIssueNameArgument),//
      arg.get(customIssuePatternArgument),//
      arg.get(customIssueLinkArgument));
   }

   checkArgument(//
     arg.wasGiven(outputStdoutArgument) || arg.wasGiven(outputFileArgument),//
     "You must supply an output, " + PARAM_OUTPUT_FILE + " <filename> or " + PARAM_OUTPUT_STDOUT);

   if (arg.wasGiven(outputStdoutArgument)) {
    systemOutPrintln(changelogApiBuilder.render());
   }

   if (arg.wasGiven(outputFileArgument)) {
    String filePath = arg.get(outputFileArgument);
    changelogApiBuilder.toFile(filePath);
   }

  } catch (ArgumentException exception) {
   System.out.println(exception.getMessageAndUsage());
   System.exit(1);
  }
 }

 @VisibleForTesting
 public static String getSystemOutPrintln() {
  return Main.systemOutPrintln;
 }

 @VisibleForTesting
 public static void recordSystemOutPrintln() {
  Main.recordSystemOutPrintln = true;
 }

 private static void systemOutPrintln(String systemOutPrintln) {
  if (Main.recordSystemOutPrintln) {
   Main.systemOutPrintln = systemOutPrintln;
  } else {
   System.out.println(systemOutPrintln);
  }
 }
}
