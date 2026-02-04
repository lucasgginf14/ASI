// 1. Importamos las rutas de la carpeta "guest"
import guestRoutes from "./guest/routes";

// 2. Importamos las rutas de la carpeta "host"
import hostRoutes from "./host/routes";

export default [...guestRoutes, ...hostRoutes];
