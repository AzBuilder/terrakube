const express = require('express');
const compression = require('compression');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;
const dist = '../build';

app.use(compression());
app.use('/', express.static(`${__dirname}/${dist}`));

app.get('/*', (_, res) => {
  res.sendFile(path.join(__dirname, `/${dist}/index.html`));
});

app.listen(port);