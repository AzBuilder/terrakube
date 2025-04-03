import getVcsNameFromUrl from "./getVcsNameFromUrl";

describe("getVcsNameFromUrl", () => {
  test("DevOps url returns correct", async () => {
    const result = getVcsNameFromUrl("https://dev.azure.com/org-name/project-name/repo-name");
    expect(result).toBe("org-name/project-name/repo-name");
  });

  test("GitHub url returns correct", async () => {
    const result = getVcsNameFromUrl("https://github.com/org-name/repo-name");
    expect(result).toBe("org-name/repo-name");
  });

  test("GitLab url returns correct", async () => {
    const result = getVcsNameFromUrl("https://gitlab.com/org-name/project-name/repo-name");
    expect(result).toBe("org-name/project-name/repo-name");
  });

  test("BitBucket url returns correct", async () => {
    const result = getVcsNameFromUrl("https://bitbucket.org/org-name/repo-name");
    expect(result).toBe("org-name/repo-name");
  });
});
