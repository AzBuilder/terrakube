import { Layout } from "antd";
import { useState } from "react";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { useAuth } from "../../config/authConfig";
import Login from "../Login/Login";
import { CreateModule } from "../Modules/Create";
import { ModuleDetails } from "../Modules/Details";
import { ModuleList } from "../Modules/List";
import { CreateOrganization } from "../Organizations/Create";
import { OrganizationSettings } from "../Settings/Settings";
import { CreateWorkspace } from "../Workspaces/Create";
import { WorkspaceDetails } from "../Workspaces/Details";
import { ImportWorkspace } from "../Workspaces/Import";
import "./App.css";
import MainMenu from "./MainMenu";
import { ProfilePicture } from "./ProfilePicture";
import logo from "./white_logo.png";
import { UserSettingsPage } from "@/modules/user/UserSettingsPage";
import OrganizationsPickerPage from "@/modules/organizations/OrganizationsPickerPage";
import OrganizationsDetailPage from "@/modules/organizations/OrganizationDetailsPage";

const { Header, Footer } = Layout;

const App = () => {
  const auth = useAuth();
  const [organizationName, setOrganizationName] = useState("...");
  const expiry = auth?.user?.expires_at;

  // Checking with the expiry time in the localstorage and when it has crossed the access has been revoked so It will clear the local storage and by default with no localstorage object it will route to login page.
  if (auth.isAuthenticated && auth?.user && expiry !== undefined && Math.floor(Date.now() / 1000) > expiry) {
    localStorage.clear();
  }

  if (!auth.isAuthenticated) {
    return <Login />;
  }

  return (
    <Router>
      <Layout className="layout mh-100">
        <Header>
          <a>
            <img className="logo" src={logo} alt="Logo"></img>
          </a>
          <div className="menu">
            <MainMenu organizationName={organizationName} setOrganizationName={setOrganizationName} />
          </div>
          <div className="user">
            <ProfilePicture />
          </div>
        </Header>

        <Routes>
          <Route path="/" element={<OrganizationsPickerPage />} />
          <Route
            path="/organizations/create"
            element={<CreateOrganization setOrganizationName={setOrganizationName} />}
          />
          <Route
            path="/organizations/:id/workspaces"
            element={
              <OrganizationsDetailPage setOrganizationName={setOrganizationName} organizationName={organizationName} />
            }
          />
          <Route path="/workspaces/create" element={<CreateWorkspace />} />
          <Route path="/workspaces/import" element={<ImportWorkspace />} />
          <Route path="/workspaces/:id" element={<WorkspaceDetails setOrganizationName={setOrganizationName} />} />
          <Route
            path="/organizations/:orgid/workspaces/:id"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} />}
          />
          <Route
            path="/workspaces/:id/runs"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="2" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/runs"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="2" />}
          />
          <Route
            path="/workspaces/:id/runs/:runid"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="2" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/runs/:runid"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="2" />}
          />
          <Route
            path="/workspaces/:id/states"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="3" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/states"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="3" />}
          />
          <Route
            path="/workspaces/:id/variables"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="4" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/variables"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="4" />}
          />
          <Route
            path="/workspaces/:id/schedules"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="5" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/schedules"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="5" />}
          />
          <Route
            path="/workspaces/:id/settings"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="6" />}
          />
          <Route
            path="/organizations/:orgid/workspaces/:id/settings"
            element={<WorkspaceDetails setOrganizationName={setOrganizationName} selectedTab="6" />}
          />
          <Route path="/organizations/:orgid/registry/create" element={<CreateModule />} />
          <Route
            path="/organizations/:orgid/registry"
            element={<ModuleList setOrganizationName={setOrganizationName} organizationName={organizationName} />}
          />
          <Route
            path="/organizations/:orgid/registry/:id"
            element={<ModuleDetails organizationName={organizationName} />}
          />
          <Route path="/organizations/:orgid/settings" element={<OrganizationSettings />} />
          <Route path="/organizations/:orgid/settings/general" element={<OrganizationSettings selectedTab="1" />} />
          <Route path="/organizations/:orgid/settings/teams" element={<OrganizationSettings selectedTab="2" />} />
          <Route path="/organizations/:orgid/settings/vcs" element={<OrganizationSettings selectedTab="4" />} />
          <Route
            path="/organizations/:orgid/settings/vcs/new/:vcsName"
            element={<OrganizationSettings selectedTab="4" vcsMode="new" />}
          />
          <Route path="/settings/tokens" element={<UserSettingsPage />} />
          <Route path="/organizations/:orgid/settings/ssh" element={<OrganizationSettings selectedTab="6" />} />
          <Route path="/organizations/:orgid/settings/tags" element={<OrganizationSettings selectedTab="7" />} />
          <Route path="/organizations/:orgid/settings/actions" element={<OrganizationSettings selectedTab="8" />} />
        </Routes>
        <Footer style={{ textAlign: "center" }}>Terrakube {window._env_.REACT_APP_TERRAKUBE_VERSION} ©{new Date().getFullYear()}</Footer>
      </Layout>
    </Router>
  );
};

export default App;
