export function compareVersions(a, b) {
    if (a === b) {
      return 0;
    }
    let splitA = a.replace("v","").split('.');
    let splitB = b.replace("v","").split('.');
    const length = Math.max(splitA.length, splitB.length);
    for (let i = 0; i < length; i++) {
    if (parseInt(splitA[i]) > parseInt(splitB[i]) ||
      ((splitA[i] === splitB[i]) && isNaN(splitB[i + 1]))) {
      return 1;
    }
    if (parseInt(splitA[i]) < parseInt(splitB[i]) || 
      ((splitA[i] === splitB[i]) && isNaN(splitA[i + 1]))) {
      return -1;
      }
    }
}