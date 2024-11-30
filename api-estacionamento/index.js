const express = require("express");
const bodyParser = require("body-parser");

const cors = require("cors");
const app = express();
const port = 8081;
//importações
const proprietario = require("./controllers/ProprietarioController.js");
const veiculo = require("./controllers/VeiculoController.js");
//Rotas
app.use(bodyParser.json());
//Função CORS para a autorização do uso da API 
app.use(cors());
app.get("/", (req, res) => res.send("Estou aqui"));
app.use("/proprietario", proprietario);
app.use("/veiculo", veiculo);
app.listen(port, () => console.log(`Servidor rodando porta ${port}!`));
