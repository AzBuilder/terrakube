import { Button } from 'antd';
import { React } from 'react';
import "./Home.css"

export const Home = () => {
  return(
    <div className="HomeButtons">
      <Button type="primary" shape="round" href="/organizations">Organizations</Button>
    </div>
  )
}