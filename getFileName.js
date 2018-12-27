/**
 * 12/27 布丁丸子酱
 * 
 * 批量生成plugin需要加载文件的config
 */
const fs = require('fs')

const scanDirPath = 'src/ios/res/'
const outputFileName = './test1.txt'

const formatterForObjCSrc = (inputStr) => {
  let resultStr = ''
  if (inputStr[inputStr.length - 1] === 'h') {
    resultStr = `<header-file src="${inputStr}" />`
  } else if (inputStr[inputStr.length - 1] === 'm') {
    resultStr = `<source-file src="${inputStr}" />`
  }
  return resultStr
}

const formatterResources = (inputStr) => {
  return `<resource-file src="${inputStr}" />`
}

const writeResultIntoFile = (data) => {
  fs.writeFileSync(outputFileName, data)
}

const getFormatFileName = (dirPath, formatter) => {
  const resultList = []
  const files = fs.readdirSync(dirPath)
  files.forEach(item => {
    const itemPath = dirPath + item
    let stat = fs.lstatSync(itemPath)
    if (stat.isDirectory() === true) { 
      resultList.push(...getFormatFileName(itemPath + '/', formatter))
    } else {
      if (formatter(itemPath) !== '') resultList.push(formatter(itemPath))
    }
  })
  return resultList
}

const res = getFormatFileName(scanDirPath, formatterResources)
writeResultIntoFile(res.join('\n'))
