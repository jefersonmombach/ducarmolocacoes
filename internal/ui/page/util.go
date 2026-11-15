package page

import (
	"os"
	"strconv"
	"strings"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

func writeFileBytes(caminho string, dados []byte) error {
	return os.WriteFile(caminho, dados, 0644)
}

func parseDinheiroBR(texto string) domain.Dinheiro {
	limpo := strings.TrimSpace(texto)
	limpo = strings.ReplaceAll(limpo, "R$", "")
	limpo = strings.ReplaceAll(limpo, " ", "")
	limpo = strings.ReplaceAll(limpo, ".", "")
	limpo = strings.ReplaceAll(limpo, ",", ".")

	val, err := strconv.ParseFloat(limpo, 64)
	if err != nil {
		return 0
	}
	return domain.DeReais(val)
}

func parseDataBR(texto string) (time.Time, error) {
	texto = strings.TrimSpace(texto)
	return time.Parse("02/01/2006", texto)
}
